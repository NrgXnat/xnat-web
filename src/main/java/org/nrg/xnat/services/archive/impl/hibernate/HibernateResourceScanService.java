package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.io.StopTagInputHandler;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicomtools.utilities.DicomUtils;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.FileUtils;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceMitigationReport.ResourceMitigationReportBuilder;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.nrg.xnat.services.archive.ResourceScanReport.ResourceScanReportBuilder;
import org.nrg.xnat.services.archive.ResourceScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class HibernateResourceScanService extends AbstractHibernateEntityService<ResourceScanRequest, ResourceScanRequestRepository> implements ResourceScanService {
    private static final Predicate<DcmCatEntry> MISMATCHED_ENTRY                      = entry -> !StringUtils.equals(entry.getCalculatedFileName(), entry.getFile().getName());
    private static final Comparator<File>       FILES_BY_DATE                         = Comparator.comparing(file -> {
        try {
            return Files.readAttributes(file.toPath().toAbsolutePath(), BasicFileAttributes.class).creationTime();
        } catch (IOException e) {
            return null;
        }
    });
    private static final String                 TEMPLATE_REPAIR_ID                    = "repair-id-%09d-%s";
    private static final String                 PARAM_PROJECT_ID                      = "projectId";
    private static final String                 PARAM_RESOURCE_ID                     = "resourceId";
    private static final String                 QUERY_GENERATE_RESOURCE_SCAN_REQUESTS = "SELECT s.label                     AS subject_label, "
                                                                                        + "       x.label                     AS experiment_label, "
                                                                                        + "       sc.id                       AS scan_label, "
                                                                                        + "       sc.series_description       AS scan_description, "
                                                                                        + "       x.project                   AS project_id, "
                                                                                        + "       s.id                        AS subject_id, "
                                                                                        + "       x.id                        AS experiment_id, "
                                                                                        + "       sc.xnat_imagescandata_id    AS scan_id, "
                                                                                        + "       ar.xnat_abstractresource_id AS resource_id, "
                                                                                        + "       r.uri                       AS resource_uri "
                                                                                        + "FROM xnat_abstractresource ar "
                                                                                        + "         LEFT JOIN xdat_search.xhbm_resource_scan_request rns "
                                                                                        + "                   ON ar.xnat_abstractresource_id = rns.resource_id "
                                                                                        + "         LEFT JOIN xnat_resource r ON ar.xnat_abstractresource_id = r.xnat_abstractresource_id "
                                                                                        + "         LEFT JOIN xnat_imagescandata sc "
                                                                                        + "                   ON ar.xnat_imagescandata_xnat_imagescandata_id = sc.xnat_imagescandata_id "
                                                                                        + "         LEFT JOIN xnat_experimentdata x ON sc.image_session_id = x.id "
                                                                                        + "         LEFT JOIN xnat_subjectassessordata sa ON x.id = sa.id "
                                                                                        + "         LEFT JOIN xnat_subjectdata s ON sa.subject_id = s.id "
                                                                                        + "WHERE ar.label = 'DICOM' "
                                                                                        + "  AND rns.resource_id IS NULL "
                                                                                        + "  AND x.project = :" + PARAM_PROJECT_ID;
    private static final String                 QUERY_RESOURCE_ATTRIBUTES_TEMPLATE    = "SELECT %s "
                                                                                        + "FROM xnat_abstractresource ar "
                                                                                        + "         JOIN xnat_imagescandata s ON ar.xnat_imagescandata_xnat_imagescandata_id = s.xnat_imagescandata_id "
                                                                                        + "         JOIN xnat_experimentdata x ON s.image_session_id = x.id "
                                                                                        + "WHERE ar.xnat_abstractresource_id = :" + PARAM_RESOURCE_ID;
    private static final String                 QUERY_GET_RESOURCE_PROJECT            = String.format(QUERY_RESOURCE_ATTRIBUTES_TEMPLATE, "x.project");
    private static final String                 QUERY_GET_RESOURCE_EXPERIMENT         = String.format(QUERY_RESOURCE_ATTRIBUTES_TEMPLATE, "x.label AS experiment_label, s.id AS scan_id");

    private final SerializerService          _serializer;
    private final DicomFileNamer             _dicomFileNamer;
    private final SiteConfigPreferences      _preferences;
    private final NamedParameterJdbcTemplate _template;
    private final StopTagInputHandler        _stopTagInputHandler;

    @Autowired
    public HibernateResourceScanService(final SerializerService serializer, final DicomFileNamer dicomFileNamer, final SiteConfigPreferences preferences, final NamedParameterJdbcTemplate template) {
        _serializer          = serializer;
        _dicomFileNamer      = dicomFileNamer;
        _preferences         = preferences;
        _template            = template;
        _stopTagInputHandler = DicomUtils.getMaxStopTagInputHandler();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceScanRequest> getByProject(UserI requester, String projectId) throws InsufficientPrivilegesException, NotFoundException {
        validateProjectAccess(requester, projectId);
        return getDao().findByProjectId(projectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceScanRequest getByResourceId(UserI requester, int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        validateResourceAccess(requester, resourceId);
        return getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceScanRequest> queueScansForProject(final UserI requester, final String projectId) throws NotFoundException, InsufficientPrivilegesException {
        // TODO: This should really be implemented as an aspect, similar to XapiRequestMappingAspect.
        validateProjectAccess(requester, projectId);

        // TODO: Add ability to restrict by date to query
        final List<ResourceScanRequest> requests = _template.query(QUERY_GENERATE_RESOURCE_SCAN_REQUESTS, new MapSqlParameterSource(PARAM_PROJECT_ID, projectId), ResourceScanRequest.ROW_MAPPER);
        log.debug("Got {} resource scan requests for project {}", requests.size(), projectId);
        requests.forEach(this::create);
        return requests;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceScanReport scanResource(final UserI requester, final int resourceId) throws NotFoundException, InsufficientPrivilegesException {
        validateResourceAccess(requester, resourceId);

        final ResourceScanRequest       request = getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId));
        final ResourceScanReportBuilder builder = ResourceScanReport.builder().resourceScanRequestId(request.getId());

        final Path resourceUri = Paths.get(request.getResourceUri());
        try (final InputStream input = Files.newInputStream(resourceUri)) {
            final DcmCatEntries handler = new DcmCatEntries(_dicomFileNamer, _stopTagInputHandler, resourceUri);
            _serializer.parse(input, handler);

            final List<DcmCatEntry> entries = handler.getEntries();
            builder.totalEntries(entries.size());

            final Map<Boolean, List<DcmCatEntry>> validAndInvalid = entries.stream().collect(Collectors.partitioningBy(DcmCatEntry::isValidDicomFile));
            final List<DcmCatEntry>               badFiles        = validAndInvalid.get(false);
            if (!badFiles.isEmpty()) {
                builder.badFiles(badFiles.stream().map(DcmCatEntry::getFile).collect(Collectors.toList()));
            }

            final Map<String, List<DcmCatEntry>> groupedByUid = validAndInvalid.get(true).stream().collect(Collectors.groupingBy(DcmCatEntry::getUid));
            builder.uids(groupedByUid.keySet());

            final Map<Boolean, Map<String, List<DcmCatEntry>>> splitOnListSize = groupedByUid.entrySet().stream()
                                                                                             .collect(Collectors.partitioningBy(entry -> entry.getValue().size() > 1,
                                                                                                                                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            builder.mismatchedFiles(splitOnListSize.get(false).values().stream().map(list -> list.get(0)).filter(MISMATCHED_ENTRY).collect(Collectors.toMap(DcmCatEntry::getFile, DcmCatEntry::getCalculatedFileName)));

            final Map<String, Map<File, String>> duplicates = splitOnListSize.get(true).entrySet().stream()
                                                                             .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                       entry -> entry.getValue().stream().collect(Collectors.toMap(DcmCatEntry::getFile, DcmCatEntry::getCalculatedFileName))));
            if (duplicates.isEmpty()) {
                log.info("Scan catalog for resource ID {} and found {} entries for the catalog with {} UIDs total. There are no UIDs with duplicated files.", resourceId, entries.size(), groupedByUid.size());
            } else {
                log.info("Scan catalog for resource ID {} and found {} entries for the catalog with {} UIDs total and {} UIDs with duplicated files", resourceId, entries.size(), groupedByUid.size(), duplicates.size());
                builder.duplicates(duplicates);
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        final ResourceScanReport report = builder.build();
        request.setScanReport(report);
        request.setRsnStatus(NumberUtils.max(report.getTotalBadFiles(), report.getTotalMismatchedFiles(), report.getTotalDuplicates()) > 0 ? ResourceScanRequest.Status.Divergent : ResourceScanRequest.Status.Conforming);
        getDao().saveOrUpdate(request);
        return report;
    }

    @Override
    public ResourceMitigationReport repairResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        validateResourceAccess(requester, resourceId);
        final ResourceScanRequest             request    = getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId));
        final ResourceScanReport              scanReport = request.getScanReport();
        final ResourceMitigationReportBuilder builder    = ResourceMitigationReport.builder();
        builder.resourceScanRequestId(request.getId());

        final String repairId   = getRepairId(request);
        final Path   cachePath  = getCachePath(resourceId, repairId);
        final Path   sourcePath = Paths.get(request.getResourceUri()).getParent();

        final Function<File, Path>                    backupMapper = file -> cachePath.resolve(sourcePath.relativize(file.toPath()));
        final Function<Map.Entry<File, String>, Path> renameMapper = entry -> sourcePath.resolve(entry.getValue());

        try (final PrintWriter writer = new PrintWriter(new FileWriter(cachePath.resolve(repairId + ".log").toFile()))) {
            writer.println("Beginning repair from resource scan request " + request.getId() + " for resource " + request.getResourceId() + "\n");
            writer.println("Have the following items:");

            // ALL files get backed up: renames get backed and then renamed, all remaining are deleted after renaming finished.
            final Map<Path, Path> backups = new HashMap<>();
            final Map<Path, Path> moves   = new HashMap<>();

            final List<File> badFiles = scanReport.getBadFiles();
            writer.format(" * %d bad files\n", badFiles.size());
            if (!badFiles.isEmpty()) {
                backups.putAll(badFiles.stream().collect(Collectors.toMap(File::toPath, backupMapper)));
            }

            final Map<File, String> mismatchedFiles = scanReport.getMismatchedFiles();
            writer.format(" * %d mismatched files\n", mismatchedFiles.size());
            if (!mismatchedFiles.isEmpty()) {
                backups.putAll(mismatchedFiles.keySet().stream().collect(Collectors.toMap(File::toPath, backupMapper)));
                moves.putAll(mismatchedFiles.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toPath(), renameMapper)));
            }

            final Map<String, Map<File, String>> duplicates = scanReport.getDuplicates();
            writer.format(" * %d UIDs with duplicate files, %d duplicate files total\n\nIssues:\n\n", duplicates.size(), duplicates.values().stream().mapToInt(Map::size).sum());
            if (!duplicates.isEmpty()) {
                duplicates.forEach((uid, map) -> {
                    final Set<String> calculatedNames = new HashSet<>(map.values());
                    if (calculatedNames.size() > 1) {
                        writer.format(" * There are %d calculated names for UID %s, I can't fix this myself.", calculatedNames.size(), uid);
                    } else {
                        backups.putAll(map.keySet().stream().collect(Collectors.toMap(File::toPath, backupMapper)));
                        map.keySet().stream().max(FILES_BY_DATE).ifPresent(file -> moves.put(file.toPath(), sourcePath.resolve(map.get(file))));
                    }
                });
            }

            builder.removedFiles(backups.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toAbsolutePath().toFile(), entry -> entry.getValue().toAbsolutePath().toFile())));
            builder.movedFiles(moves.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toAbsolutePath().toFile(), entry -> entry.getValue().toAbsolutePath().toFile())));

            writer.format("Actions:\n\nI have %d files to backup, %d files to rename\n\nBacking up files\n", backups.size(), moves.size());
            final Map<Pair<Path, Path>, Throwable> backupErrors = new HashMap<>();
            final Map<Pair<Path, Path>, Throwable> moveErrors   = new HashMap<>();
            final Map<Path, Throwable>             deleteErrors = new HashMap<>();

            final List<Path> deletes = new ArrayList<>();
            backups.forEach((source, target) -> {
                try {
                    Files.copy(source, target);
                    writer.format(" * %s backed up to %s\n", source.toAbsolutePath(), target.toAbsolutePath());
                    deletes.add(source);
                } catch (IOException e) {
                    backupErrors.put(Pair.of(source, target), e);
                }
            });
            if (!moves.isEmpty()) {
                writer.println("\nRenaming files");
                moves.forEach((source, target) -> {
                    try {
                        Files.move(source, target);
                        writer.format(" * %s renamed to %s\n", source.toAbsolutePath(), target.toAbsolutePath());
                        deletes.remove(source);
                    } catch (IOException e) {
                        moveErrors.put(Pair.of(source, target), e);
                    }
                });
            }

            if (!deletes.isEmpty()) {
                writer.println("\nDeleting files");
                deletes.forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        deleteErrors.put(file, e);
                    }
                });
            }

            if (!backupErrors.isEmpty()) {
                writer.format("\n\nBackup errors:");
                backupErrors.forEach((pair, throwable) -> writer.println(" * " + pair.getKey().toAbsolutePath() + " to " + pair.getValue().toAbsolutePath() + ": " + throwable.getMessage()));
                writer.println();
            }
            if (!moveErrors.isEmpty()) {
                writer.format("\n\nMove errors:");
                moveErrors.forEach((pair, throwable) -> writer.println(" * " + pair.getKey().toAbsolutePath() + " to " + pair.getValue().toAbsolutePath() + ": " + throwable.getMessage()));
                writer.println();
            }
            if (!deleteErrors.isEmpty()) {
                writer.format("\n\nDelete errors:");
                deleteErrors.forEach((path, throwable) -> writer.println(" * " + path.toAbsolutePath() + ": " + throwable.getMessage()));
                writer.println();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ResourceMitigationReport report = builder.build();
        request.setMitigationReport(report);
        request.setRsnStatus(ResourceScanRequest.Status.Conforming);
        update(request);
        return report;
    }

    @Value
    @Builder
    private static class DcmCatEntry {
        String  uid;
        String  id;
        String  uri;
        File    file;
        String  calculatedFileName;
        boolean isValidDicomFile;

        public static DcmCatEntryBuilder builder(final DicomFileNamer dicomFileNamer, final StopTagInputHandler stopTagInputHandler) {
            return new ExtendedDcmCatEntryBuilder(dicomFileNamer, stopTagInputHandler);
        }

        private static class ExtendedDcmCatEntryBuilder extends DcmCatEntryBuilder {
            private final DicomFileNamer      _dicomFileNamer;
            private final StopTagInputHandler _stopTagInputHandler;

            ExtendedDcmCatEntryBuilder(final DicomFileNamer dicomFileNamer, final StopTagInputHandler stopTagInputHandler) {
                _dicomFileNamer      = dicomFileNamer;
                _stopTagInputHandler = stopTagInputHandler;
            }

            @Override
            public DcmCatEntry build() {
                // Validates required fields
                Validate.notBlank(super.id, "ID cannot be null or empty");
                Validate.notBlank(super.uid, "UID cannot be null or empty");
                Validate.notBlank(super.uri, "URI cannot be null or empty");
                Validate.notNull(super.file, "File cannot be null or empty");
                Validate.isTrue(super.file.exists() && super.file.isFile(), "File must exist and be a file");
                super.calculatedFileName(getCalculatedFileName(super.file));
                super.isValidDicomFile(StringUtils.isNotBlank(super.calculatedFileName));
                return super.build();
            }

            private String getCalculatedFileName(final File file) {
                try {
                    final DicomObject dicomObject = DicomUtils.read(file, _stopTagInputHandler);
                    if (dicomObject != null) {
                        return _dicomFileNamer.makeFileName(dicomObject);
                    }
                } catch (IOException e) {
                    log.warn("An error occurred trying to read the DICOM file {}", file.getAbsolutePath(), e);
                }
                return null;
            }
        }
    }

    @Getter
    @Accessors(prefix = "_")
    private static class DcmCatEntries extends DefaultHandler {
        private static final String QNAME_ID       = "ID";
        private static final String QNAME_UID      = "UID";
        private static final String QNAME_URI      = "URI";
        private static final String QNAME_XSI_TYPE = "xsi:type";
        public static final  String CAT_ENTRY      = "cat:entry";
        public static final  String CAT_DCM_ENTRY  = "cat:dcmEntry";

        private final DicomFileNamer      _dicomFileNamer;
        private final StopTagInputHandler _stopTagInputHandler;
        private final Path                _rootPath;
        private final List<DcmCatEntry>   _entries;

        public DcmCatEntries(final DicomFileNamer dicomFileNamer, final StopTagInputHandler stopTagInputHandler, final Path resourceUri) {
            _dicomFileNamer      = dicomFileNamer;
            _stopTagInputHandler = stopTagInputHandler;

            // Make the root path the folder containing the resource URI if the URI indicates a file, otherwise use as is
            _rootPath = resourceUri.toFile().isFile() ? resourceUri.getParent() : resourceUri;
            _entries  = new ArrayList<>();
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
            // If we have a cat:entry element with xsi:type set to cat:dcmEntry...
            if (StringUtils.equals(CAT_ENTRY, qName) && StringUtils.equals(CAT_DCM_ENTRY, attributes.getValue(QNAME_XSI_TYPE))) {
                final String path = attributes.getValue(QNAME_URI);
                final File   file = _rootPath.resolve(path).toFile();
                _entries.add(DcmCatEntry.builder(_dicomFileNamer, _stopTagInputHandler).id(attributes.getValue(QNAME_ID)).uri(path).uid(attributes.getValue(QNAME_UID)).file(file).build());
            }
        }
    }

    /**
     * Creates or retrieves a path to a cache folder under the system cache folder, with the subfolder path
     * <i>projectId</i>/mitigation/<i>experimentLabel</i>/<i>scanId</i>.
     *
     * @param resourceId The resource to create or retrieve a cache path.
     *
     * @return The requested cache path.
     *
     * @throws NotFoundException When the project or resource ID can't be found.
     */
    private Path getCachePath(final int resourceId, final String repairId) throws NotFoundException {
        final String               projectId = getResourceProject(resourceId);
        final Pair<String, String> pair      = getResourceExperimentAndScan(resourceId);
        final Path cachePath = Paths.get(_preferences.getCachePath())
                                    .resolve(projectId)
                                    .resolve(repairId)
                                    .resolve(pair.getKey())
                                    .resolve(pair.getValue());
        if (!cachePath.toFile().exists()) {
            cachePath.toFile().mkdirs();
        }
        return cachePath;
    }

    /**
     * Verifies that the specified resource exists and that the user has sufficient access to the project containing the
     * resource.
     *
     * @param requester  The user to check
     * @param resourceId The resource to check
     *
     * @throws NotFoundException               When the specified resource does not exist
     * @throws InsufficientPrivilegesException When the specified user does not have sufficient privileges on the project
     */
    private void validateResourceAccess(final UserI requester, final int resourceId) throws NotFoundException, InsufficientPrivilegesException {
        validateProjectAccess(requester, getResourceProject(resourceId));
    }

    /**
     * Verifies that the specified project exists and that the user has sufficient access to that project.
     *
     * @param requester The user to check
     * @param projectId The project to check
     *
     * @throws NotFoundException               When the specified project does not exist
     * @throws InsufficientPrivilegesException When the specified user does not have sufficient privileges on the project
     */
    private void validateProjectAccess(final UserI requester, final String projectId) throws NotFoundException, InsufficientPrivilegesException {
        // TODO: This should really be implemented as an aspect, similar to XapiRequestMappingAspect.
        if (!Permissions.verifyProjectExists(_template, projectId)) {
            throw new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId);
        }
        if (!Permissions.canDeleteProject(requester, projectId)) {
            throw new InsufficientPrivilegesException(requester.getUsername(), projectId);
        }
    }

    /**
     * Gets the project with which a particular resource is associated.
     *
     * @param resourceId The ID resource to evaluate
     *
     * @return The ID of the associated project if available.
     */
    private String getResourceProject(final int resourceId) throws NotFoundException {
        try {
            return _template.queryForObject(QUERY_GET_RESOURCE_PROJECT, new MapSqlParameterSource(PARAM_RESOURCE_ID, resourceId), String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId);
        }
    }

    /**
     * Gets the experiment with which a particular resource is associated.
     *
     * @param resourceId The ID resource to evaluate
     *
     * @return The ID of the associated project if available.
     */
    private Pair<String, String> getResourceExperimentAndScan(final int resourceId) throws NotFoundException {
        try {
            return _template.query(QUERY_GET_RESOURCE_EXPERIMENT, new MapSqlParameterSource(PARAM_RESOURCE_ID, resourceId),
                                   result -> result.next()
                                             ? Pair.of(result.getString("experiment_label"), result.getString("scan_id"))
                                             : ImmutablePair.nullPair());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId);
        }
    }

    private static String getRepairId(final ResourceScanRequest request) {
        return String.format(TEMPLATE_REPAIR_ID, request.getId(), FileUtils.getMsTimestamp());
    }
}
