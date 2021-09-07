package org.nrg.xnat.services.prearchive.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.dcm.Dcm2Jpg;
import org.nrg.framework.constants.PrearchiveCode;
import org.nrg.xapi.exceptions.*;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.*;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.PermissionsServiceImpl;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.services.PermissionsServiceI;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.predicates.ProjectAccessPredicate;
import org.nrg.xnat.dto.prearchive.PrearcSessionResourceDto;
import org.nrg.xnat.dto.prearchive.PrearcSessionScanDto;
import org.nrg.xnat.dto.prearchive.PrearcSessionScanResFileDto;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.helpers.file.StoredFile;
import org.nrg.xnat.helpers.merge.MergeUtils;
import org.nrg.xnat.helpers.prearchive.*;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.helpers.transactions.HTTPSessionStatusManagerQueue;
import org.nrg.xnat.helpers.transactions.PersistentStatusQueueManagerI;
import org.nrg.xnat.helpers.uri.UriParserUtils.UriParser;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.actions.importer.ImporterNotFoundException;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.restlet.util.XNATRestConstants;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;
import org.nrg.xnat.services.prearchive.PrearchiveService;
import org.nrg.xnat.services.prearchive.util.PrearcInfoUtil;
import org.nrg.xnat.status.StatusList;
import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.functions.Functions;
import org.nrg.xnat.utils.functions.UriToSessionDataTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static org.nrg.xnat.archive.Operation.*;

@Service
@Slf4j
public class PrearchiveServiceImpl implements PrearchiveService {

    public static final String PARAM_OVERRIDE_LOCK = "overrideLock";

    public static final String HTTP_SESSION_LISTENER   = "http-session-listener";
    public static final String APPLICATION_DICOM       = "application/dicom";
    public static final String APPLICATION_XMIRC       = "application/x-mirc";
    public static final String APPLICATION_XMIRC_DICOM = "application/x-mirc-dicom";
    public static final String SESSION_IMPORTER        = "SI";
    public static final String XAR_IMPORTER            = "XAR";
    public static final String GRADUAL_DICOM_IMPORTER  = "gradual-DICOM";
    public static final String DICOM_INBOX_IMPORTER    = "inbox";
    public static final String DICOM_ZIP_IMPORTER      = "DICOM-zip";

    @Autowired
    public PrearchiveServiceImpl(final NamedParameterJdbcTemplate template) {
        _template = template;
        _permissions = XDAT.getContextService().getBean(PermissionsServiceImpl.class);
    }

    @Override
    public List<PrearchiveDto> findAllPrearchives(UserI user, String projectId, String tag) throws SQLException, SessionException, Exception {
        boolean dataAccess = Groups.hasAllDataAccess(user);
        if (StringUtils.isNotBlank(tag)) {
            List<PrearchiveDto>           prearchiveDtos = new ArrayList<>();
            Collection<SessionDataTriple> result         = Lists.transform(new ArrayList<>(PrearcDatabase.getSessionByUID(tag)), Functions.SESSION_DATA_TO_SESSION_DATA_TRIPLE);
            if (Objects.isNull(result)) {
                throw new NotFoundException("Session Data Triple result wasn't found");
            } else {
                for (final SessionDataTriple s : result) {
                    String query = DatabaseSession.findSessionSql(s.getFolderName(), s.getTimestamp(), s.getProject());
                    if (StringUtils.isNotBlank(query)) {
                        PrearchiveDto prearchiveDto = _template.queryForObject(query, new MapSqlParameterSource(), new PrearchiveRowMapper());
                        prearchiveDtos.add(prearchiveDto);
                    }
                }
                return prearchiveDtos;
            }
        } else {
            final List<String> projects = new ArrayList<>(StringUtils.isNotBlank(projectId) ? Arrays.asList(projectId.split("\\s*,\\s*")) : _permissions.getUserEditableProjects(user.getUsername()));
            if (Objects.isNull(projects) && projects.size() <= 0) {
                throw new NotFoundException("List of project ID wasn't found");
            }
            if (dataAccess) {
                projects.add(null);
            }
            return _template.query(DatabaseSession.PROJECT.allMatchesSql(projects.toArray(new String[0])), new MapSqlParameterSource(), new PrearchiveRowMapper());
        }
    }


    @Override
    public PrearchiveDto createPrarchiveRebuild(UserI user, List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException, DataFormatException {
        final List<SessionDataTriple> triples = getSessionDataTriples(user, src);
        if (triples == null) {
            return null;
        }

        for (final SessionDataTriple triple : triples) {
            try {
                if (PrearcDatabase.setStatus(triple.getFolderName(), triple.getTimestamp(), triple.getProject(), PrearcUtils.PrearcStatus.QUEUED_BUILDING, overrideLock)) {
                    XDAT.sendJmsRequest(new PrearchiveOperationRequest(user, Rebuild, triple, _additionalValues));
                } else {
                    log.warn("Tried to reset the status of the session {} to QUEUED_BUILDING, but failed. This usually means the session is locked and the override lock parameter was false. This might be OK: I checked whether the session was locked before trying to update the status but maybe a new file arrived in the intervening millisecond(s).", triple);
                }
            } catch (IllegalArgumentException e) {
                throw new DataFormatException(e.getMessage());
            } catch (InvalidPermissionException e) {
                throw new InsufficientPrivilegesException(e.getMessage());
            } catch (Exception exception) {
                log.error("Error when setting prearchive session {} status to QUEUED for user {}", triple.toString(), user.getUsername(), exception);
                throw new InitializationException(exception);
            }
        }

        PrearchiveDto result = getPrearchiveResult(triples);
        if (Objects.isNull(result)) {
            throw new NotFoundException("Prearchive rebuild wasn't found");
        }
        return result;
    }


    @Override
    public PrearchiveDto deletePrarchive(UserI user, List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException {
        final List<SessionDataTriple> triples = getSessionDataTriples(user, src);
        if (triples == null) {
            return null;
        }

        for (final SessionDataTriple triple : triples) {
            try {
                if (PrearcDatabase.setStatus(triple.getFolderName(), triple.getTimestamp(), triple.getProject(), PrearcUtils.PrearcStatus.QUEUED_DELETING)) {
                    final SessionData session    = PrearcDatabase.getSession(triple.getFolderName(), triple.getTimestamp(), triple.getProject());
                    final File        sessionDir = PrearcUtils.getPrearcSessionDir(user, triple.getProject(), triple.getTimestamp(), triple.getFolderName(), false);
                    XDAT.sendJmsRequest(new PrearchiveOperationRequest(user, Delete, session, sessionDir));
                }
            } catch (Exception e) {
                throw new InitializationException(e.getMessage());
            }
        }
        PrearchiveDto result = getPrearchiveResult(triples);
        if (Objects.isNull(result)) {
            throw new NotFoundException("Prearchive delete wasn't found");
        }
        return result;
    }

    @Override
    public PrearchiveDto movePrarchive(UserI user, List<String> src, String newProject) throws InitializationException, InsufficientPrivilegesException, NotFoundException, ResourceAlreadyExistsException, DataFormatException {

        final List<SessionDataTriple> triples = getSessionDataTriples(user, src);
        if (triples == null) {
            return null;
        }
        for (final SessionDataTriple triple : triples) {
            try {
                if (PrearcDatabase.setStatus(triple.getFolderName(), triple.getTimestamp(), triple.getProject(), PrearcUtils.PrearcStatus.QUEUED_MOVING)) {
                    final SessionData session    = PrearcDatabase.getSession(triple.getFolderName(), triple.getTimestamp(), triple.getProject());
                    final File        sessionDir = PrearcUtils.getPrearcSessionDir(user, triple.getProject(), triple.getTimestamp(), triple.getFolderName(), false);

                    final Map<String, Object> parameters = new HashMap<>();
                    parameters.put(PrearchiveOperationRequest.PARAM_DESTINATION, newProject);

                    XDAT.sendJmsRequest(new PrearchiveOperationRequest(user, Move, session, sessionDir, parameters));
                }
            } catch (SessionException e) {
                errorResponse(e, triple);
            } catch (Exception e) {
                log.error("", e);
                throw new InitializationException(e.getMessage());
            }
        }
        PrearchiveDto result = getPrearchiveResult(triples);
        if (Objects.isNull(result)) {
            throw new NotFoundException("Prearchive move wasn't found");
        }
        return result;
    }

    @Override
    public List<PrearcSessionScanResFileDto> findAllPrearcSessionResourceByScanIdAndResourceId(UserI user, String projectId, String timestamp, String sessionLabel, Integer scanId, String resourceId, String filepath, boolean prettyPrint, HttpServletRequest request) throws ActionException, NotFoundException, DataFormatException {
        final PrearcInfoUtil info;
        info = PrearcInfoUtil.retrieveSessionBean(user, projectId, timestamp, sessionLabel);
        String                   project = info.session.getProject();
        final XnatImagescandataI scan    = MergeUtils.getMatchingScanById(scanId.toString(), (List<XnatImagescandataI>) info.session.getScans_scan());
        if (Objects.isNull(scan)) {
            throw new NotFoundException("scan data wasn't found");
        }
        final XnatResourcecatalogI res = (XnatResourcecatalogI) MergeUtils.getMatchingResourceByLabel(resourceId, scan.getFile());
        if (Objects.isNull(res)) {
            throw new NotFoundException("Resource data wasn't found");
        }
        final CatalogUtils.CatalogData catalogData;
        try {
            catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(info.session.getPrearchivepath(), res, false, project);
        } catch (ServerException e) {
            throw new NotFoundException("Catalog data wasn't found");
        }

        final String      rootPath = catalogData.catPath;
        final CatCatalogI catalog  = catalogData.catBean;

        if (StringUtils.isNotEmpty(filepath)) {
            return getPrearchiveSessionScanResWithFilepath(catalog, filepath, rootPath, project, resourceId, request, prettyPrint);
        } else {
            return getPrearchiveSessionScanRes(catalog, rootPath, project, prettyPrint, request);
        }
    }


    @Override
    public List<String> importFiles(UserI user, HttpServletRequest request, XnatResourceInfo xnatResourceInfo) throws DataFormatException, ServerException, ClientException, NotFoundException {
        boolean prearchive = true;
        fw.clear();
        response = new ArrayList<>();
        fw.add(xnatResourceInfo);
        // Set the overwrite flag if we are uploading directly to the archive (prearchive_code = 1)
        String prearchive_code = (String) params.get("prearchive_code");
        if ("1".equals(prearchive_code)) { // User has selected archive option
            prearchive = false;
            params = getSelectedArchiveOption(user);
        }

        ImporterHandlerA importer = null;
        if (fw.size() == 0 && handler != null && !HANDLERS_ALLOWING_CALLS_WITHOUT_FILES.contains(handler)) {
            throw new DataFormatException("Unable to identify upload format.");
        } else if (handler != null && fw.size() == 0) {
            response = callImporter(importer, user, request);
        } else if (fw.size() > 1) {
            throw new DataFormatException("Importer is limited to one uploaded resource at a time.");
        }
        if (handler == null && xnatResourceInfo != null) {
            if (APPLICATION_DICOM.equals(request.getContentType()) || APPLICATION_XMIRC.equals(request.getContentType()) || APPLICATION_XMIRC_DICOM.equals(request.getContentType())) {
                handler = ImporterHandlerA.GRADUAL_DICOM_IMPORTER;
            }
        }

        importer = getImporter(importer, user);

        storeStatusList(importer, request);

        response = getResponseWithCallImport(importer, prearchive, xnatResourceInfo, request);

        return response;
    }

    private List<PrearcSessionScanResFileDto> getPrearchiveSessionScanRes(CatCatalogI catalog, String rootPath, String project, boolean prettyPrint, HttpServletRequest request) {
        List<PrearcSessionScanResFileDto> sessionScanResources = new ArrayList<>();
        for (final CatEntryI entry : CatalogUtils.getEntriesByFilter(catalog, null)) {
            File f = CatalogUtils.getFile(entry, rootPath, project);
			if (f == null) {
				continue;
			}
            sessionScanResources = getSessionScanResources(f, entry, request, sessionScanResources, prettyPrint);
        }
        return sessionScanResources;
    }

    private List<PrearcSessionScanResFileDto> getSessionScanResources(File f, CatEntryI entry, HttpServletRequest request, List<PrearcSessionScanResFileDto> sessionScanResources, boolean prettyPrint) {
        sessionScanResources.add(PrearcSessionScanResFileDto.builder()
                                                            .name(f.getName())
                                                            .uri(constructURI(entry.getUri(), request))
                                                            .size((prettyPrint) ? Long.valueOf(CatalogUtils.formatSize(f.length())) : f.length()).build());
        return sessionScanResources;
    }

    private List<PrearcSessionScanResFileDto> getPrearchiveSessionScanResWithFilepath(CatCatalogI catalog, String filepath, String rootPath, String project, String resourceId, HttpServletRequest request, boolean prettyPrint) throws DataFormatException {
        List<PrearcSessionScanResFileDto> sessionScanResources = new ArrayList<>();
        final CatEntryI                   entry                = CatalogUtils.getEntryByURI(catalog, filepath);
        File                              f                    = CatalogUtils.getFile(entry, rootPath, project);
		if (f == null) {
			return null;
		}

        if (request.getContentType().equals("image/jpg") && StringUtils.equals(resourceId, "DICOM") && Dcm2Jpg.isDicom(f)) {
            try {
                InputStream inputStream = new ByteArrayInputStream(Dcm2Jpg.convert(f));
                File        file        = new File("");
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                //return new InputRepresentation(new ByteArrayInputStream(Dcm2Jpg.convert(f)), mt);
            } catch (IOException e) {
                throw new DataFormatException("Unable to convert this file to jpeg : " + e.getMessage());
            }
        }
        return getSessionScanResources(f, entry, request, sessionScanResources, prettyPrint);

    }

    private String constructURI(String resource, HttpServletRequest request) {
        String requestPart = request.getServletPath() + request.getPathInfo();
        return requestPart + "/" + resource;

    }

    @Override
    public List<PrearcSessionResourceDto> findAllPrearcSessionResourceByScanId(UserI user, String projectId, String timestamp, String sessionLabel, Integer scanId) throws ActionException, NotFoundException {
        List<PrearcSessionResourceDto> sessionResources = new ArrayList<>();
        final PrearcInfoUtil           info;
        info = PrearcInfoUtil.retrieveSessionBean(user, projectId, timestamp, sessionLabel);
        String                   project        = info.session.getProject();
        String                   prearchivePath = info.session.getPrearchivepath();
        final XnatImagescandataI scan           = MergeUtils.getMatchingScanById(scanId.toString(), (List<XnatImagescandataI>) info.session.getScans_scan());
        if (Objects.isNull(scan)) {
            throw new NotFoundException("scan data wasn't found");
        }
        for (final XnatAbstractresourceI res : scan.getFile()) {
            sessionResources = getSessionResourcesByScanId(prearchivePath, project, res, sessionResources);
        }
        return sessionResources;
    }


    @Override
    public List<PrearcSessionScanDto> findAllPrearcSessionScans(UserI user, String projectId, String timestamp, String sessionLabel) throws ActionException {
        List<PrearcSessionScanDto> prearcSessionScanDtos = new ArrayList<>();
        final PrearcInfoUtil       info;
        info = PrearcInfoUtil.retrieveSessionBean(user, projectId, timestamp, sessionLabel);
        for (XnatImagescandataI scan : info.session.getScans_scan()) {
            prearcSessionScanDtos.add(PrearcSessionScanDto.builder()
                                                          .ID(scan.getId())
                                                          .xsiType(scan.getXSIType())
                                                          .series_description(scan.getSeriesDescription()).build());
        }
        return prearcSessionScanDtos;
    }


    @Override
    public List<PrearcSessionResourceDto> findAllPrearcSessionResource(UserI user, String projectId, String timestamp, String sessionLabel) throws ActionException {
        List<PrearcSessionResourceDto> prearcSessionResourceDtos = new ArrayList<>();
        final PrearcInfoUtil           info;
        info = PrearcInfoUtil.retrieveSessionBean(user, projectId, timestamp, sessionLabel);
        String project        = info.session.getProject();
        String prearchivePath = info.session.getPrearchivepath();
        for (final XnatImagescandataI scan : info.session.getScans_scan()) {
            prearcSessionResourceDtos = getPrearcSessionResource(project, prearchivePath, scan, prearcSessionResourceDtos);
        }
        return prearcSessionResourceDtos;
    }


    private List<PrearcSessionResourceDto> getSessionResourcesByScanId(String prearchivePath, String project, XnatAbstractresourceI res, List<PrearcSessionResourceDto> sessionResources) throws ServerException {
        final CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(prearchivePath, (XnatResourcecatalogI) res, false, project);
        CatalogUtils.Stats             stats       = CatalogUtils.getFileStats(catalogData.catBean, catalogData.catPath, catalogData.project);
        sessionResources.add(PrearcSessionResourceDto.builder()
                                                     .label(res.getLabel())
                                                     .file_count(Long.valueOf(stats.count))
                                                     .file_size(stats.size).build());
        return sessionResources;
    }


    private List<PrearcSessionResourceDto> getPrearcSessionResource(String project, String prearchivePath, XnatImagescandataI scan, List<PrearcSessionResourceDto> sessionResources) {
        for (final XnatAbstractresourceI res : scan.getFile()) {
            if (res instanceof XnatResourcecatalogI) {
                return getPrearcSessionXnatResourceCatalogI(project, prearchivePath, res, scan, sessionResources);
            } else if (res instanceof XnatResourceI) {
                return getPrearcSessionXnatResourceI(prearchivePath, res, scan, sessionResources);
            }
        }
        return sessionResources;
    }

    private List<PrearcSessionResourceDto> getPrearcSessionXnatResourceI(String prearchivePath, XnatAbstractresourceI res, XnatImagescandataI scan, List<PrearcSessionResourceDto> sessionResources) {
        File f = new File(prearchivePath, ((XnatResourceI) res).getUri());
        if (f.exists()) {
            return getSessionResources(CATEGORY_NAME, scan.getId(), res.getLabel(), ONE_FILE_COUNT, f.length(), sessionResources);
        } else {
            return getSessionResources(CATEGORY_NAME, scan.getId(), res.getLabel(), ZERO_FILE_COUNT, ZERO_FILE_SIZE, sessionResources);
        }
    }

    private List<PrearcSessionResourceDto> getPrearcSessionXnatResourceCatalogI(String project, String prearchivePath, XnatAbstractresourceI res, XnatImagescandataI scan, List<PrearcSessionResourceDto> sessionResources) {
        try {
            final CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(prearchivePath, (XnatResourcecatalogI) res, false, project
                                                                                                     );
            CatalogUtils.Stats stats = CatalogUtils.getFileStats(catalogData.catBean, catalogData.catPath, catalogData.project);
            return getSessionResources(CATEGORY_NAME, scan.getId(), res.getLabel(), Long.valueOf(stats.count), stats.size, sessionResources);
        } catch (ServerException e) {
            log.error("Unable to read catalog for resource {}", res.getXnatAbstractresourceId(), e);
        }
        return sessionResources;
    }

    private List<PrearcSessionResourceDto> getSessionResources(String catagory, String catId, String label, Long fileCount, Long fileSize, List<PrearcSessionResourceDto> sessionResources) {
        sessionResources.add(PrearcSessionResourceDto.builder()
                                                     .category(catagory)
                                                     .cat_id(catId)
                                                     .label(label)
                                                     .file_count(fileCount)
                                                     .file_size(fileSize).build());
        return sessionResources;
    }


    private void errorResponse(SessionException e, SessionDataTriple triple) throws ResourceAlreadyExistsException, DataFormatException, NotFoundException, InsufficientPrivilegesException {
        switch (e.getError()) {
            case AlreadyExists:
                throw new ResourceAlreadyExistsException("A prearchive resource with session " + triple.getFolderName() + " and timestamp " + triple.getTimestamp() + " already exists in the project " + triple.getProject(), null);
            case DoesntExist:
                throw new NotFoundException("No prearchive resource with session " + triple.getFolderName() + " and timestamp " + triple.getTimestamp() + " exists in the project " + triple.getProject());
            case NoProjectSpecified:
                throw new DataFormatException("No project specified to move session " + triple.getFolderName() + " and timestamp " + triple.getTimestamp());
            case InvalidStatus:
                throw new InsufficientPrivilegesException("Can't move session " + triple.getFolderName() + " and timestamp " + triple.getTimestamp() + " in project " + triple.getProject() + " as it has an invalid status");
            case InvalidSession:
                throw new InsufficientPrivilegesException("The session " + triple.getFolderName() + " and timestamp " + triple.getTimestamp() + " in project " + triple.getProject() + " is invalid (it's not missing, but something's wrong with it)");
            case DatabaseError:
                throw new InsufficientPrivilegesException("A database error occurred trying to move the session " + triple.getFolderName() + " and timestamp " + triple.getTimestamp() + " in project " + triple.getProject());
        }
    }

    protected List<SessionDataTriple> getSessionDataTriples(UserI user, List<String> src) throws InitializationException, InsufficientPrivilegesException, NotFoundException {
        final UriToSessionDataTriple  transformer = new UriToSessionDataTriple();
        final List<SessionDataTriple> triples     = Lists.transform(src, transformer);
        if (transformer.hasMalformedUrls()) {
            throw new InitializationException("One or more source values contained a malformed URL: " + StringUtils.join(transformer.getMalformedUrls(), ", "));
        }

        final Pair<List<String>, List<String>> deniedAndMissing = getDeniedAndMissingProjectsFromPrearcSources(user, triples);
        final List<String>                     denied           = deniedAndMissing.getLeft();
        final List<String>                     missing          = deniedAndMissing.getRight();
        if (!denied.isEmpty()) {
            if (!missing.isEmpty()) {
                throw new InsufficientPrivilegesException("Invalid permissions for user " + user.getUsername() + " to delete prearchive sessions in one or more projects: " + StringUtils.join(denied, ", ") + ". Also found one or more missing projects: " + StringUtils.join(missing, ", ") + ".");
            } else {
                throw new InsufficientPrivilegesException("Invalid permissions for user " + user.getUsername() + " to delete prearchive sessions in one or more projects: " + StringUtils.join(denied, ", "));
            }
        }
        if (!missing.isEmpty()) {
            throw new NotFoundException("One or more specified projects does not exist: " + StringUtils.join(missing, ", ") + ".");
        }
        return triples;
    }

    protected static Pair<List<String>, List<String>> getDeniedAndMissingProjectsFromPrearcSources(final UserI user, final Collection<SessionDataTriple> triples) {
        final ProjectAccessPredicate predicate = new ProjectAccessPredicate(XDAT.getContextService().getBean(PermissionsServiceI.class), XDAT.getNamedParameterJdbcTemplate(), user, AccessLevel.Edit);
        final List<String>           missing   = predicate.getMissing();
        final List<String> denied = Lists.newArrayList(Iterables.filter(Iterables.filter(Iterables.transform(triples, FUNCTION_SESSION_DATA_TRIPLE_TO_PROJECT_ID), Predicates.not(predicate)), new Predicate<String>() {
            @Override
            public boolean apply(final String projectId) {
                return !missing.contains(projectId);
            }
        }));
        return ImmutablePair.of(denied, missing);
    }


    private PrearchiveDto getPrearchiveResult(List<SessionDataTriple> triples) {
        for (final SessionDataTriple s : triples) {
            String query = DatabaseSession.findSessionSql(s.getFolderName(), s.getTimestamp(), s.getProject());
            if (StringUtils.isNotBlank(query)) {
                return _template.queryForObject(query, new MapSqlParameterSource(), new PrearchiveRowMapper());
            }
        }
        return null;
    }

    private static final Function<SessionDataTriple, String> FUNCTION_SESSION_DATA_TRIPLE_TO_PROJECT_ID = new Function<SessionDataTriple, String>() {
        @Nullable
        @Override
        public String apply(final SessionDataTriple triple) {
            return triple.getProject();
        }
    };


    private static class PrearchiveRowMapper implements RowMapper<PrearchiveDto> {
        @Override
        public PrearchiveDto mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            return PrearchiveDto.getPrearchiveData(resultSet, rsmd);
        }
    }


    private List<String> getResponseWithCallImport(ImporterHandlerA importer, boolean prearchive, XnatResourceInfo xnatResourceInfo, HttpServletRequest request) throws ClientException, ServerException {
        ThreadPoolExecutor importerExecutorService;
        if (httpSessionListener && async && (importerExecutorService = XDAT.getContextService().getBeanSafely("threadPoolExecutorFactoryBean", ThreadPoolExecutor.class)) != null) {
            String task = prearchive ? "prearchival" : "archival";
            importerExecutorService.submit(importer);
        } else {
            response = importer.call();
        }
        return response;
    }


    private ImporterHandlerA getImporter(ImporterHandlerA importer, UserI user) throws ServerException, ClientException, DataFormatException, NotFoundException {
        try {
            importer = ImporterHandlerA.buildImporter(handler, listenerControl, user, fw.get(0), params);
        } catch (SecurityException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("", e);
            throw new ServerException(e.getMessage(), e);
        } catch (IllegalArgumentException | NoSuchMethodException e) {
            log.error("", e);
            throw new DataFormatException(e.getMessage(), e);
        } catch (ImporterNotFoundException e) {
            log.error("", e);
            throw new NotFoundException(e.getMessage());
        }
        return importer;
    }


    private List<String> callImporter(ImporterHandlerA importer, UserI user, HttpServletRequest request) throws ClientException, ServerException, DataFormatException {
        try {
            // FileWriterWrapperI is null because no files should have been uploaded.
            importer = ImporterHandlerA.buildImporter(handler, listenerControl, user, null, params);
        } catch (Exception e) {
            log.error("", e);
            throw new ServerException(e.getMessage(), e);
        }
        storeStatusList(importer, request);

        return importer.call();
    }

    public boolean storeStatusList(final ImporterHandlerA importer, HttpServletRequest request) throws DataFormatException {
        if (httpSessionListener) {
            if (StringUtils.isEmpty(listenerControl)) {
                throw new DataFormatException(XNATRestConstants.TRANSACTION_RECORD_ID + "' is required when requesting '" + HTTP_SESSION_LISTENER + "'.");
            }
            final StatusList sq = new StatusList();
            importer.addStatusListener(sq);
            storeStatusList(listenerControl, sq, request);
        }
        return false;
    }

    private Map<String, Object> getSelectedArchiveOption(UserI user) throws DataFormatException {
        // If the overwrite flag has been set by the user, make sure it is a valid option
        if (params.containsKey("overwrite")) {
            String ow = (String) params.get("overwrite");
            if (!PrearcUtils.DELETE.equalsIgnoreCase(ow) || !PrearcUtils.APPEND.equalsIgnoreCase(ow)) {
                throw new DataFormatException("Overwrite flag was not set to a valid option. ('append' or 'delete')");
            }
            // If the overwrite flag has not been set by the user, set the flag based on
            // the project setting.
        } else {
            // Get the prearchive code for the project specified.
            XnatProjectdata proj  = XnatProjectdata.getProjectByIDorAlias((String) params.get("project"), user, true);
            PrearchiveCode  pCode = PrearchiveCode.code(proj.getArcSpecification().getPrearchiveCode());

            // If the project is set to auto archive overwrite
            if (pCode == PrearchiveCode.AutoArchiveOverwrite) {
                params.put("overwrite", PrearcUtils.DELETE);
            } else { // If the project is set to append or prearchive-only.
                params.put("overwrite", PrearcUtils.APPEND);
            }
        }
        return params;
    }

    protected void storeStatusList(final String transaction_id, final StatusList sl, HttpServletRequest request) throws IllegalArgumentException {
        retrieveSQManager(request).storeStatusQueue(transaction_id, sl);
    }

    protected PersistentStatusQueueManagerI retrieveSQManager(HttpServletRequest request) {
        return new HTTPSessionStatusManagerQueue(getHttpSession(request));
    }

    public HttpSession getHttpSession(HttpServletRequest request) {
        return request.getSession();
    }

    public void handleParam(String key, Object value, UserI user) throws ClientException, DataFormatException, NotFoundException {
        switch (key) {
            case ImporterHandlerA.IMPORT_HANDLER_ATTR:
                handler = (String) value;
                break;
            case XNATRestConstants.TRANSACTION_RECORD_ID:
                listenerControl = (String) value;
                break;
            case "src":
                fw.add(retrievePrestoreFile((String) value, user));
                async = true;
                break;
            case HTTP_SESSION_LISTENER:
                listenerControl = (String) value;
                httpSessionListener = true;
                break;
            default:
                params.put(key, value);
                break;
        }
    }

    public FileWriterWrapperI retrievePrestoreFile(final String src, UserI user) throws ClientException, DataFormatException, NotFoundException {
        //Template.MODE_STARTS_WITH =1
        //Map<String, Object> map = new UriParser("/user/cache/resources/{XNAME}/files/", Template.MODE_STARTS_WITH).readUri(src);
        Map<String, Object> map = new UriParser("/user/cache/resources/{XNAME}/files/", 1).readUri(src);

        if (!map.containsKey("XNAME") || !map.containsKey("_REMAINDER")) {
            throw new DataFormatException("src uri is invalid.", new Exception());
        }
        File f = org.nrg.xdat.security.helpers.Users.getUserCacheFile(user, (String) map.get("XNAME"), (String) map.get("_REMAINDER"));

        if (f.exists()) {
            return new StoredFile(f, true);
        } else {
            throw new NotFoundException("Unknown src file.");
        }
    }


    String                   handler             = null;
    String                   listenerControl     = null;
    boolean                  httpSessionListener = false;
    boolean                  async               = false; // when importing a previously uploaded file, no need to keep the connection open while we import
    Map<String, Object>      params              = new Hashtable<>();
    List<FileWriterWrapperI> fw                  = new ArrayList<>();
    List<String>             response            = null;
    private static final List<String> HANDLERS_ALLOWING_CALLS_WITHOUT_FILES = Lists.newArrayList();
    private static final List<String> HANDLERS_PREFERRING_PARTIAL_URI_WRAP  = Lists.newArrayList();

    private final        NamedParameterJdbcTemplate _template;
    private final        PermissionsServiceImpl     _permissions;
    private final        Map<String, Object>        _additionalValues = new HashMap<>();
    private static final String                     CATEGORY_NAME     = "scans";
    private static final Long                       ONE_FILE_COUNT    = 1L;
    private static final Long                       ZERO_FILE_COUNT   = 0L;
    private static final Long                       ZERO_FILE_SIZE    = 0L;

}
