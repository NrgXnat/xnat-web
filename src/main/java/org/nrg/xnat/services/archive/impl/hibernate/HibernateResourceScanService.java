package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dcm4che2.io.StopTagInputHandler;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicomtools.utilities.DicomUtils;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.nrg.xnat.services.archive.ResourceScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
@Slf4j
public class HibernateResourceScanService extends AbstractHibernateEntityService<ResourceScanRequest, ResourceScanRequestRepository> implements ResourceScanService {
    private static final String TEMPLATE_EXPERIMENT_URI                  = "/data/archive/experiments/%s/scans/%s";
    private static final String PARAM_PROJECT_ID                         = "projectId";
    private static final String PARAM_RESOURCE_ID                        = "resourceId";
    private static final String TEMPLATE_GENERATE_RESOURCE_SCAN_REQUESTS = "SELECT s.label                     AS subject_label, "
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
                                                                           + "  %s";
    private static final String QUERY_GENERATE_PROJECT_SCAN_REQUESTS     = String.format(TEMPLATE_GENERATE_RESOURCE_SCAN_REQUESTS, "  AND x.project = :" + PARAM_PROJECT_ID);
    private static final String QUERY_GENERATE_RESOURCE_SCAN_REQUEST     = String.format(TEMPLATE_GENERATE_RESOURCE_SCAN_REQUESTS, "  AND ar.xnat_abstractresource_id = :" + PARAM_RESOURCE_ID);
    private static final String QUERY_RESOURCE_ATTRIBUTES_TEMPLATE       = "SELECT %s "
                                                                           + "FROM xnat_abstractresource ar "
                                                                           + "         JOIN xnat_imagescandata s ON ar.xnat_imagescandata_xnat_imagescandata_id = s.xnat_imagescandata_id "
                                                                           + "         JOIN xnat_experimentdata x ON s.image_session_id = x.id "
                                                                           + "WHERE ar.xnat_abstractresource_id = :" + PARAM_RESOURCE_ID;
    private static final String QUERY_GET_RESOURCE_PROJECT               = String.format(QUERY_RESOURCE_ATTRIBUTES_TEMPLATE, "x.project");
    private static final String QUERY_GET_RESOURCE_EXPERIMENT            = String.format(QUERY_RESOURCE_ATTRIBUTES_TEMPLATE, "x.label AS experiment_label, s.id AS scan_id");

    private final CatalogService             _catalogService;
    private final SerializerService          _serializer;
    private final DicomFileNamer             _dicomFileNamer;
    private final StopTagInputHandler        _stopTagInputHandler;
    private final SiteConfigPreferences      _preferences;
    private final NamedParameterJdbcTemplate _template;

    @Autowired
    public HibernateResourceScanService(final CatalogService catalogService, final SerializerService serializer, final DicomFileNamer dicomFileNamer, final SiteConfigPreferences preferences, final NamedParameterJdbcTemplate template) {
        _catalogService      = catalogService;
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
    public ResourceScanRequest createResourceScanRequest(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        // TODO: This should really be implemented as an aspect, similar to XapiRequestMappingAspect.
        validateResourceAccess(requester, resourceId);

        final ResourceScanRequest request = _template.queryForObject(QUERY_GENERATE_RESOURCE_SCAN_REQUEST, new MapSqlParameterSource(PARAM_RESOURCE_ID, resourceId), ResourceScanRequest.ROW_MAPPER);
        log.debug("Created a resource scan request for resource {}", resourceId);
        return create(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceScanRequest> createResourceScanRequests(final UserI requester, final String projectId) throws NotFoundException, InsufficientPrivilegesException {
        // TODO: This should really be implemented as an aspect, similar to XapiRequestMappingAspect.
        validateProjectAccess(requester, projectId);

        // TODO: Add ability to restrict by date to query
        final List<ResourceScanRequest> requests = _template.query(QUERY_GENERATE_PROJECT_SCAN_REQUESTS, new MapSqlParameterSource(PARAM_PROJECT_ID, projectId), ResourceScanRequest.ROW_MAPPER);
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

        final ResourceScanRequest request = getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId));

        final ResourceScanHelper helper = new ResourceScanHelper(request, _serializer, _dicomFileNamer, _stopTagInputHandler);
        final ResourceScanReport report = helper.call();

        request.setScanReport(report);
        request.setRsnStatus(NumberUtils.max(report.getTotalBadFiles(), report.getTotalMismatchedFiles(), report.getTotalDuplicates()) > 0 ? ResourceScanRequest.Status.Divergent : ResourceScanRequest.Status.Conforming);
        getDao().saveOrUpdate(request);
        return report;
    }

    @Override
    public ResourceMitigationReport repairResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException, InitializationException {
        validateResourceAccess(requester, resourceId);

        final ResourceScanRequest request   = getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId));
        final String              repairId  = request.generateRepairId();
        final Path                cachePath = getCachePath(resourceId, repairId);

        final ResourceRepairHelper     helper = new ResourceRepairHelper(request, cachePath, repairId);
        final ResourceMitigationReport report = helper.call();
        request.setMitigationReport(report);
        request.setRsnStatus(ResourceScanRequest.Status.Conforming);
        update(request);

        final String resourceUri = String.format(TEMPLATE_EXPERIMENT_URI, request.getExperimentId(), request.getScanLabel());
        try {
            _catalogService.refreshResourceCatalog(requester, resourceUri);
            log.info("Refreshed the catalog for resource ID {} at URL {}", resourceId, resourceUri);
        } catch (ServerException | ClientException e) {
            throw new InitializationException("An error occurred trying to refresh the resource catalog for ID " + resourceId + " with URI " + resourceUri, e);
        }

        return report;
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
}
