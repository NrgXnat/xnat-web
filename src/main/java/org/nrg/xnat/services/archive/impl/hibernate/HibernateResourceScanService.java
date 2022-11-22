package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dcm4che2.io.StopTagInputHandler;
import org.jetbrains.annotations.NotNull;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dicomtools.utilities.DicomUtils;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.exceptions.ConflictedStateException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.nrg.xnat.services.archive.ResourceScanService;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.nrg.xft.event.persist.PersistentWorkflowUtils.QUEUED;

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
                                                                           + "       e.element_name              AS xsi_type, "
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
                                                                           + "         LEFT JOIN xdat_meta_element e ON x.extension = e.xdat_meta_element_id  "
                                                                           + "         LEFT JOIN xnat_subjectassessordata sa ON x.id = sa.id "
                                                                           + "         LEFT JOIN xnat_subjectdata s ON sa.subject_id = s.id "
                                                                           + "WHERE ar.label = 'DICOM' "
                                                                           + "  AND rns.resource_id IS NULL "
                                                                           + "  %s";
    private static final String QUERY_GENERATE_PROJECT_SCAN_REQUESTS     = String.format(TEMPLATE_GENERATE_RESOURCE_SCAN_REQUESTS, "  AND x.project = :" + PARAM_PROJECT_ID);
    private static final String QUERY_GENERATE_RESOURCE_SCAN_REQUEST     = String.format(TEMPLATE_GENERATE_RESOURCE_SCAN_REQUESTS, "  AND ar.xnat_abstractresource_id = :" + PARAM_RESOURCE_ID);
    private static final String QUERY_GET_RESOURCE_PROJECT               = "SELECT x.project "
                                                                           + "FROM xnat_abstractresource ar "
                                                                           + "         JOIN xnat_imagescandata s ON ar.xnat_imagescandata_xnat_imagescandata_id = s.xnat_imagescandata_id "
                                                                           + "         JOIN xnat_experimentdata x ON s.image_session_id = x.id "
                                                                           + "WHERE ar.xnat_abstractresource_id = :" + PARAM_RESOURCE_ID;

    private final CatalogService             _catalogService;
    private final SerializerService          _serializer;
    private final DicomFileNamer             _dicomFileNamer;
    private final StopTagInputHandler        _stopTagInputHandler;
    private final SiteConfigPreferences      _preferences;
    private final NamedParameterJdbcTemplate _jdbcTemplate;
    private final JmsTemplate                _jmsTemplate;

    @Autowired
    public HibernateResourceScanService(final CatalogService catalogService, final SerializerService serializer, final DicomFileNamer dicomFileNamer, final SiteConfigPreferences preferences, final NamedParameterJdbcTemplate jdbcTemplate, final JmsTemplate jmsTemplate) {
        _catalogService      = catalogService;
        _serializer          = serializer;
        _dicomFileNamer      = dicomFileNamer;
        _stopTagInputHandler = DicomUtils.getMaxStopTagInputHandler();
        _preferences         = preferences;
        _jdbcTemplate        = jdbcTemplate;
        _jmsTemplate         = jmsTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceScanRequest> getByProject(final UserI requester, final String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return getByProject(requester, projectId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceScanRequest> getByProject(final UserI requester, final String projectId, final ResourceScanRequest.Status status) throws InsufficientPrivilegesException, NotFoundException {
        validateProjectAccess(requester, projectId);
        return status == null ? getDao().findByProjectId(projectId) : getDao().findByProjectIdAndStatus(projectId, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceScanRequest getByResourceId(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException {
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

        final ResourceScanRequest request = _jdbcTemplate.queryForObject(QUERY_GENERATE_RESOURCE_SCAN_REQUEST, new MapSqlParameterSource(PARAM_RESOURCE_ID, resourceId), ResourceScanRequest.ROW_MAPPER);
        request.setRequester(requester.getUsername());
        log.debug("User {} created a resource scan request for resource {}", requester.getUsername(), resourceId);
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
        final List<ResourceScanRequest> requests = _jdbcTemplate.query(QUERY_GENERATE_PROJECT_SCAN_REQUESTS, new MapSqlParameterSource(PARAM_PROJECT_ID, projectId), ResourceScanRequest.ROW_MAPPER);
        log.debug("Got {} resource scan requests for project {}", requests.size(), projectId);
        requests.forEach(request -> {
            request.setRequester(requester.getUsername());
            create(request);
        });
        return requests;
    }

    @Override
    public List<ResourceScanReport> scanResources(final UserI requester, final String projectId) throws InsufficientPrivilegesException, NotFoundException {
        final Map<ResourceScanRequest, ConflictedStateException> exceptions = new HashMap<>();
        final List<ResourceScanRequest>                          requests   = getByProject(requester, projectId, ResourceScanRequest.Status.Created);
        try {
            return requests.stream().map(request -> {
                               try {
                                   return runScanForRequest(request);
                               } catch (ConflictedStateException e) {
                                   exceptions.put(request, e);
                                   return null;
                               }
                           })
                           .filter(Objects::nonNull)
                           .collect(Collectors.toList());
        } finally {
            if (!exceptions.isEmpty()) {
                log.error("{} errors occurred trying to queue repair requests for project {}", exceptions.size(), projectId);
                exceptions.forEach((request, e) -> log.error("Resource ID {} for subject {} and experiment {} (ID: {}), resource URI: {}", request.getResourceId(), request.getSubjectLabel(), request.getExperimentLabel(), request.getExperimentId(), request.getResourceUri(), e));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceScanReport scanResource(final UserI requester, final int resourceId) throws NotFoundException, InsufficientPrivilegesException, ConflictedStateException {
        validateResourceAccess(requester, resourceId);
        return runScanForRequest(getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> queueRepairResourcesForProject(final UserI requester, final String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return queueRepairResourcesForProject(requester, projectId, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> queueRepairResourcesForProject(final UserI requester, final String projectId, final String reason, final String comment) throws InsufficientPrivilegesException, NotFoundException {
        final Map<ResourceScanRequest, InitializationException> exceptions = new HashMap<>();
        try {
            final List<ResourceScanRequest> requests = getByProject(requester, projectId, ResourceScanRequest.Status.Divergent);
            log.info("User {} wants to queue requests with divergent scans for project {} and found {} such requests", requester.getUsername(), projectId, requests.size());
            return requests.stream().map(request -> {
                               try {
                                   return queueRepairRequest(requester, request, reason, comment);
                               } catch (InitializationException e) {
                                   exceptions.put(request, e);
                                   return -1;
                               }
                           })
                           .filter(workflowId -> workflowId > 0)
                           .collect(Collectors.toList());
        } finally {
            if (!exceptions.isEmpty()) {
                log.error("{} errors occurred trying to queue repair requests for project {}", exceptions.size(), projectId);
                exceptions.forEach((request, e) -> log.error("Resource ID {} for subject {} and experiment {} (ID: {}), resource URI: {}", request.getResourceId(), request.getSubjectLabel(), request.getExperimentLabel(), request.getExperimentId(), request.getResourceUri(), e));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer queueRepairResource(final UserI requester, final int resourceId) throws NotFoundException, InsufficientPrivilegesException, InitializationException {
        return queueRepairResource(requester, resourceId, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer queueRepairResource(final UserI requester, final int resourceId, final String reason, final String comment) throws InsufficientPrivilegesException, NotFoundException, InitializationException {
        validateResourceAccess(requester, resourceId);
        return queueRepairRequest(requester, getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId)), reason, comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void repairResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException, InitializationException {
        validateResourceAccess(requester, resourceId);

        final ResourceScanRequest request = getDao().findByResourceId(resourceId).orElseThrow(() -> new NotFoundException(ResourceScanRequest.class.getSimpleName(), resourceId));
        if (request.getRsnStatus() != ResourceScanRequest.Status.QueuedForRepair) {
            if (request.getRsnStatus() == ResourceScanRequest.Status.Created) {
                log.info("Got a request to repair resource {} but the status for that request is \"Queued\": you should scan this resource first", resourceId);
            } else {
                log.info("Got a request to repair resource {} but the status for that request is {} (should be \"QueuedForRepair\" to repair)", resourceId, request.getRsnStatus());
            }
            return;
        }
        final WrkWorkflowdata workflow = WrkWorkflowdata.getWrkWorkflowdatasByWrkWorkflowdataId(request.getWorkflowId(), requester, false);
        setStatus(request, workflow, ResourceScanRequest.Status.Repairing);

        final Path cachePath = getCachePath(request);

        final ResourceRepairHelper     helper = new ResourceRepairHelper(request, cachePath);
        final ResourceMitigationReport report = helper.call();
        request.setMitigationReport(report);
        setStatus(request, workflow, ResourceScanRequest.Status.Conforming);

        final String resourceUri = String.format(TEMPLATE_EXPERIMENT_URI, request.getExperimentId(), request.getScanLabel());
        try {
            _catalogService.refreshResourceCatalog(requester, resourceUri);
            log.info("Refreshed the catalog for resource ID {} at URL {}", resourceId, resourceUri);
        } catch (ServerException | ClientException e) {
            throw new InitializationException("An error occurred trying to refresh the resource catalog for ID " + resourceId + " with URI " + resourceUri, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepairStatus(final UserI requester, final int workflowId) throws NotFoundException {
        return Optional.ofNullable(WrkWorkflowdata.getWrkWorkflowdatasByWrkWorkflowdataId(workflowId, requester, false))
                       .orElseThrow(() -> new NotFoundException(WrkWorkflowdata.SCHEMA_ELEMENT_NAME, workflowId))
                       .getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, String> getRepairStatuses(final UserI requester, final List<Integer> workflowIds) {
        return workflowIds.stream().map(workflowId -> {
                              try {
                                  return Pair.of(workflowId, getRepairStatus(requester, workflowId));
                              } catch (NotFoundException e) {
                                  log.warn("User {} requested status for workflow ID {}, but couldn't find an item: {}", requester.getUsername(), workflowId, e.getMessage());
                                  return null;
                              }
                          })
                          .filter(Objects::nonNull)
                          .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @NotNull
    private ResourceScanReport runScanForRequest(final ResourceScanRequest request) throws ConflictedStateException {
        if (request.getRsnStatus() != ResourceScanRequest.Status.Created) {
            throw new ConflictedStateException("The resource scan request for resource " + request.getRequester() + " should be \"Queued\" but instead is \"" + request.getRsnStatus() + "\"");
        }

        request.setRsnStatus(ResourceScanRequest.Status.Scanning);
        getDao().update(request);

        final ResourceScanHelper helper = new ResourceScanHelper(request, _serializer, _dicomFileNamer, _stopTagInputHandler);
        final ResourceScanReport report = helper.call();

        request.setScanReport(report);
        request.setRsnStatus(NumberUtils.max(report.getTotalBadFiles(), report.getTotalMismatchedFiles(), report.getTotalDuplicates()) > 0 ? ResourceScanRequest.Status.Divergent : ResourceScanRequest.Status.Conforming);
        getDao().update(request);
        return report;
    }

    private void setStatus(final ResourceScanRequest request, final WrkWorkflowdata workflow, final ResourceScanRequest.Status status) {
        request.setRsnStatus(status);
        update(request);
        workflow.setStatus(status.toString());
        try {
            WorkflowUtils.save(workflow, workflow.buildEvent());
        } catch (Exception e) {
            log.error("An error occurred trying to update the status of workflow {}", workflow.getWrkWorkflowdataId(), e);
        }
    }

    /**
     * Creates or retrieves a path to a cache folder under the system cache folder, with the subfolder path
     * <i>projectId</i>/repair-<i>requestId (in hex form)</i>/<i>experimentLabel</i>/<i>scanId</i>.
     *
     * @param request The resource scan request for which to create or retrieve a cache path.
     *
     * @return The requested cache path.
     */
    private Path getCachePath(final ResourceScanRequest request) {
        final Path cachePath = Paths.get(_preferences.getCachePath())
                                    .resolve(request.getProjectId())
                                    .resolve(String.format("repair-%08x", request.getId()))
                                    .resolve(request.getExperimentLabel())
                                    .resolve(request.getScanLabel());
        if (!cachePath.toFile().exists()) {
            cachePath.toFile().mkdirs();
        }
        return cachePath;
    }

    /**
     * Creates a workflow entry, associates it with the {@link ResourceScanRequest scan request}, then pushes the request
     * onto the processing queue.
     *
     * @param requester The user to check
     * @param request   The resource scan request to be queued.
     * @param reason    The reason for running the request.
     * @param comment   A comment for the request.
     *
     * @return The workflow ID.
     *
     * @throws InitializationException When an error occurs building the workflow instance.
     */
    private int queueRepairRequest(UserI requester, ResourceScanRequest request, String reason, String comment) throws InitializationException {
        final PersistentWorkflowI workflow = buildRepairRequestWorkflow(requester, request, reason, comment);
        request.setWorkflowId(workflow.getWorkflowId());
        request.setRsnStatus(ResourceScanRequest.Status.QueuedForRepair);
        getDao().update(request);
        XDAT.sendJmsRequest(_jmsTemplate, request);
        return workflow.getWorkflowId();
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
        if (!Permissions.verifyProjectExists(_jdbcTemplate, projectId)) {
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
            return _jdbcTemplate.queryForObject(QUERY_GET_RESOURCE_PROJECT, new MapSqlParameterSource(PARAM_RESOURCE_ID, resourceId), String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId);
        }
    }

    private static PersistentWorkflowI buildRepairRequestWorkflow(final UserI requester, final ResourceScanRequest request, final String reason, final String comment) throws InitializationException {
        try {
            final PersistentWorkflowI workflow = PersistentWorkflowUtils.buildOpenWorkflow(requester, request.getXsiType(), request.getExperimentId(), request.getScanLabel(), request.getProjectId(), EventUtils.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.TYPE.REST, "Repair resource", reason, comment));
            workflow.setStatus(QUEUED);
            workflow.setSrc(Integer.toString(request.getResourceId()));
            final EventMetaI event = workflow.buildEvent();
            PersistentWorkflowUtils.save(workflow, event);
            return workflow;
        } catch (PersistentWorkflowUtils.JustificationAbsent e) {
            throw new InitializationException("You must provide a justification/reason for the repair resource operation");
        } catch (PersistentWorkflowUtils.ActionNameAbsent e) {
            throw new InitializationException("You must provide an action for the repair resource operation");
        } catch (Exception e) {
            throw new InitializationException("An unknown error occurred trying to create a workflow for repair request for resource " + request.getResourceId(), e);
        }
    }
}
