package org.nrg.xapi.rest.experiments;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.*;
import org.nrg.xdat.model.*;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.experiments.ExperimentService;
import org.nrg.xnat.services.experiments.ImageAssessorService;
import org.nrg.xnat.services.scans.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api("XNAT experiment Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ExperimentApi extends AbstractXapiProjectRestController {
    @Autowired
    public ExperimentApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ExperimentService experimentService, final ImageAssessorService assessorService, final ScanService scanService) {
        super(userManagementService, roleHolder);
        _experimentService = experimentService;
        _assessorService = assessorService;
        _scanService = scanService;
    }

    @ApiOperation(value = "Gets the requested  experiment", notes = "Returns the  experiment with the specified ID", response = XnatExperimentdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested experiment."),
                   @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
                   @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatExperimentdataI getById(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested experiment with ID {} }", getSessionUser().getUsername(), experimentId);
        return _experimentService.findById(getSessionUser(), experimentId).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, experimentId));
    }

    @ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
                   @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatExperimentdataI> getAllExperiments() throws NotFoundException {
        log.debug("User {} requested experiments }", getSessionUser().getUsername());
        return _experimentService.findAll(getSessionUser());
    }

    @ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
                   @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
                   @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatExperimentdataI> getAllByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested experiment with projectId {} }", getSessionUser().getUsername(), projectId);
        return _experimentService.findAllByProjectId(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Get single experiment", notes = "The experiments function returns a single experiment configured in the XNAT system.", response = XnatExperimentdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
                   @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
                   @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatExperimentdataI getByIdAndProject(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                 @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested experiment with projectId {} and with ID {} }", getSessionUser().getUsername(), projectId, experimentId);
        return _experimentService.findByProjectIdAndExperimentId(getSessionUser(), projectId, experimentId).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, projectId));
    }

    @ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred"),
                   @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
                   @ApiResponse(code = 404, message = "The requested experiment wasn't found.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatExperimentdataI> getAllByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                   @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested experiment with projectId {} and with subjectId {}}", getSessionUser().getUsername(), projectId, subjectId);
        return _experimentService.findAllByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId);
    }

    @ApiOperation(value = "Delete an existing experiment", notes = "Deletes the specified experiment.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified experiment."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete experiments in the specified experiment"),
                   @ApiResponse(code = 404, message = "The specified experiment or experiment doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/experiments/{experimentId}", "/experiments/{experimentId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE, restrictTo = AccessLevel.Delete)
    public void deleteExperiment(@ApiParam("The ID of the experiment to be deleted") @PathVariable(required = false) @Project final String projectId,
                                 @ApiParam("The ID of the experiment to be deleted") @PathVariable @Experiment final String experimentId,
                                 @ApiParam("The file path value") @RequestParam(required = false) String filepath,
                                 @ApiParam("The removeFiles value ") @RequestParam(defaultValue = "false") boolean removeFiles,
                                 @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                 @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                 @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                 @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                                 @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws DataFormatException, NotFoundException {
        log.debug("User {} delete experiment with projectId {} and with experimentId {}}", getSessionUser().getUsername(), projectId, experimentId);
        _experimentService.deleteById(getSessionUser(), projectId, experimentId, filepath, removeFiles, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    @ApiOperation(value = "Update an existing experiment", notes = "Updates the submitted experiment.", response = XnatSubjectassessordataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated experiment."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit experiment in the specified project"),
                   @ApiResponse(code = 404, message = "The specified experiment doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = PUT, restrictTo = AccessLevel.Edit)
    public XnatExperimentdataI updateExperiment(@ApiParam("The project containing the subject to be updated") @PathVariable @Project final String projectId,
                                                @ApiParam("The subject in which the experiment should be created") @PathVariable @Subject final String subjectId,
                                                @ApiParam("The ID of the experiment to be updated") @PathVariable @Experiment final String experimentId,
                                                @ApiParam("The subject to be updated.") @RequestBody final XnatExperimentdataI experiment,
                                                @ApiParam("The data allow to be delete") @RequestParam(required = false) String allowDataDelete,
                                                @ApiParam("The label value.") @RequestParam(required = false) String label,
                                                @ApiParam("The filepath value.") @RequestParam(defaultValue = "") String filepath,
                                                @ApiParam("The primary value.") @RequestParam(required = false) String primary,
                                                @ApiParam("The moveAssessors value.") @RequestParam(required = false) String moveAssessors,
                                                @ApiParam("The overwrite value.") @RequestParam(defaultValue = "false") boolean overwrite,
                                                @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                                @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                                @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                                @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                                                @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment,
                                                @ApiParam("The fixScanTypes value ") @RequestParam(defaultValue = "false", required = false) boolean fixScanTypes,
                                                @ApiParam("The pullDataFromHeaders value ") @RequestParam(defaultValue = "false", required = false) boolean pullDataFromHeaders,
                                                @ApiParam("The trigger Pipelines value ") @RequestParam(defaultValue = "false", required = false) boolean triggerPipelines,
                                                @ApiParam("The suppress Email value ") @RequestParam(defaultValue = "false", required = false) boolean suppressEmail) throws DataFormatException {
        if (StringUtils.isNotBlank(projectId) && !StringUtils.equals(experiment.getProject(), projectId)) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the experiment is assigned to project " + experiment.getProject() + ". These values must be the same.");
        }
        if (!StringUtils.equals(experimentId, experiment.getId())) {
            throw new DataFormatException("You specified the subject ID " + experimentId + " in your request but the experiment to be updated has the ID " + experiment.getId() + ". These values must be the same.");
        }
        log.debug("Controller Api- Update experiment {} (ID {}) in project {}", experiment.getLabel(), experimentId, experiment.getProject());
        return _experimentService.update(getSessionUser(), experiment, experimentId, projectId, subjectId, allowDataDelete, label, primary, moveAssessors, overwrite, filepath, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment),
                                         fixScanTypes, pullDataFromHeaders, triggerPipelines, suppressEmail);
    }

    @ApiOperation(value = "Create a new experiment", notes = "Creates the submitted experiment.", response = XnatExperimentdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created experiment."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create experiment in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST, restrictTo = AccessLevel.Edit)
    public XnatExperimentdataI createExperiment(@ApiParam("The project in which the experiment should be created") @PathVariable @Project final String projectId,
                                                @ApiParam("The subject in which the experiment should be created") @PathVariable @Subject final String subjectId,
                                                @ApiParam("The subject to be created.") @RequestBody final XnatExperimentdataI experiment,
                                                @ApiParam("The xsiType value") @RequestParam(required = false) String xsiType,
                                                @ApiParam("The allowDataDelete value") @RequestParam(defaultValue = "false") String allowDataDelete,
                                                @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                                @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                                @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                                @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                                                @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment,
                                                @ApiParam("The trigger Pipelines value ") @RequestParam(defaultValue = "false", required = false) boolean triggerPipelines,
                                                @ApiParam("The suppress Email value ") @RequestParam(defaultValue = "false", required = false) boolean suppressEmail) throws DataFormatException, NotFoundException {
        final boolean experimentHasProject = StringUtils.isNotBlank(experiment.getProject());
        final boolean hasProject           = StringUtils.isNotBlank(projectId);
        if (!experimentHasProject && !hasProject) {
            throw new DataFormatException("You must specify a project in which the experiment should be created.");
        }
        if (experimentHasProject && hasProject && !StringUtils.equals(experiment.getProject(), projectId)) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the experiment is assigned to project " + experiment.getProject() + ". These values must be the same.");
        }
        if (!experimentHasProject) {
            experiment.setProject(projectId);
        }
        return _experimentService.create(getSessionUser(), experiment, projectId, subjectId, xsiType, allowDataDelete, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment), triggerPipelines, suppressEmail);
    }

    @ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatImageassessordataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested assessors wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatImageassessordataI> getAllByProjectIdAndSubjectIdAndExperimentId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                                     @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                                                     @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested assessor with project ID {} , with subject ID {} and with experiment ID {} }", getSessionUser().getUsername(), projectId, subjectId, experimentId);
        return _assessorService.findAllByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId, experimentId);
    }

    @ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a experiment configured in the XNAT system.", response = XnatImageassessordataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the currently configured experiment."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or assessorId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested assessor wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatImageassessordataI getByIdAndProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                                              @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                                                              @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                                              @ApiParam(value = "The ID of the assessor.") @PathVariable final String assessorId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested assessor with project ID {} , with subject ID {} , with experiment ID {} and with assessor ID {}  }", getSessionUser().getUsername(), projectId, subjectId, experimentId, assessorId);
        return _assessorService.findByIdAndProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId, experimentId, assessorId).orElseThrow(() -> new NotFoundException(XnatImageassessordata.SCHEMA_ELEMENT_NAME, assessorId));
    }

    @ApiOperation(value = "Get list of assessors", notes = "The experiments function returns a list of all assessors configured in the XNAT system.", response = XnatImageassessordataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured assessors."),
                   @ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested assessors wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatImageassessordataI> getAllByExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested assessor with experiment ID {} }", getSessionUser().getUsername(), experimentId);
        return _assessorService.findAllByExperimentId(getSessionUser(), experimentId);
    }

    @ApiOperation(value = "Get list of assessors", notes = "The experiments function returns a assessor configured in the XNAT system.", response = XnatImageassessordataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured assessors."),
                   @ApiResponse(code = 400, message = "The requested either assessorId or experimentId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested assessor wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatImageassessordataI getByAssessorIdAndExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                 @ApiParam(value = "The ID of the assessor.") @PathVariable final String assessorId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested assessor with experiment ID {} and with assessor ID {}  }", getSessionUser().getUsername(), experimentId, assessorId);
        return _assessorService.findByIdAndExperimentId(getSessionUser(), assessorId, experimentId).orElseThrow(() -> new NotFoundException(XnatImageassessordata.SCHEMA_ELEMENT_NAME, experimentId));
    }

    @ApiOperation(value = "Gets the Scan Quality Label", notes = "Returns the scan quality label", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested scan quality label wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/services/scan-quality-labels", "/services/scan-quality-labels/{projectId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public String getAllScanQualityLabel(@ApiParam("The ID of the project to be updated") @PathVariable(required = false) @Project final String projectId) throws InitializationException {
        log.debug("User {} requested Scan Quality Label", getSessionUser().getUsername());
        return _scanService.findAllScanQualityLabel(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatImagescandataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
                   @ApiResponse(code = 400, message = "The requested projectId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested scans wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatImagescandataI> getScanTypesByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
        return _scanService.findAllScanTypesByProjectId(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Get list of scan types", notes = "The scans function returns a list of all scan types configured in the XNAT system.", response = XnatImagescandataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
                   @ApiResponse(code = 404, message = "The requested scan types wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatImagescandataI> getAllScanTypes() throws NotFoundException {
        log.debug("User {} requested scan types", getSessionUser().getUsername());
        return _scanService.findAllScanTypes(getSessionUser());
    }

    @ApiOperation(value = "Get scans with specified assessed ID", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
                   @ApiResponse(code = 400, message = "The requested assessedId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested scans wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/experiments/{assessedId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatImagescandataI> getAllByAssessedId(@ApiParam(value = "The ID of the assessed.") @PathVariable @Experiment final String assessedId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested assessor with ID {}", getSessionUser().getUsername(), assessedId);
        return _scanService.findAllByAssessedId(getSessionUser(), assessedId);
    }

    @ApiOperation(value = "Get the scan with specified assessor ID", notes = "The scans function returns a scan configured in the XNAT system.", response = XnatScscandataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
                   @ApiResponse(code = 400, message = "The requested either assessedId or scanId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested scans wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatImagescandataI getByAssessedIdAndScanId(@ApiParam(value = "The ID of the assessed.") @PathVariable @Experiment final String assessedId,
                                                       @ApiParam(value = "The ID of the scan.") @PathVariable final Integer scanId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested assessor with ID {} and scan with ID {}", getSessionUser().getUsername(), assessedId, scanId);
        return _scanService.findByAssessedIdAndScanId(getSessionUser(), assessedId, scanId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, assessedId));
    }

    @ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested scans wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatImagescandataI> getScansByProjectAndSubjectAndExperiment(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                             @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                                             @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {}, subject with ID {} and experiment with ID {}", getSessionUser().getUsername(), projectId, subjectId, experimentId);
        return _scanService.findAllByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId, experimentId);
    }

    @ApiOperation(value = "Get scan of specified scanId", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or scanId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested scans wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatImagescandataI getByProjectIdAndSubjectIdAndExperimentIdAndScanId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                                 @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                                                 @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                                 @ApiParam(value = "The ID of the scan.") @PathVariable final Integer scanId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {}, subject with ID {}, experiment with ID {} and scan with ID {}", getSessionUser().getUsername(), projectId, subjectId, experimentId, scanId);
        return _scanService.findByProjectIdAndSubjectIdAndExperimentIdAndScanId(getSessionUser(), projectId, subjectId, experimentId, scanId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, experimentId));
    }

    @ApiOperation(value = "Get all scanners", notes = "The scanners function returns a list of all scanner configured in the XNAT system.", response = XnatScscandataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
                   @ApiResponse(code = 400, message = "The requested scanner  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested scanner wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/scanners", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<Map<String, String>> getAllScanners(@ApiParam(value = "The value of the scanTable.") @RequestParam(required = false) final String scanTable,
                                                    @ApiParam(value = "The ID of the project.") @RequestParam(required = false) @Project final String projectId) throws InsufficientPrivilegesException {
        log.debug("User {} requested scanners", getSessionUser().getUsername());
        return _scanService.findAllScanners(getSessionUser(), scanTable, projectId);
    }

    @ApiOperation(value = "Delete an existing scan", notes = "Deletes the specified scan.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified scan."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified scan"),
                   @ApiResponse(code = 404, message = "The specified scan or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{idOrLabel}/scans/{scanId}",
                                 "/experiments/{idOrLabel}/scans/{scanId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE, restrictTo = AccessLevel.Delete)
    public void deleteScan(@ApiParam("The ID of the project to be deleted") @PathVariable(required = false) @Project final String projectId,
                           @ApiParam("The ID of the subject to be deleted") @PathVariable(required = false) @Subject final String subjectId,
                           @ApiParam("The ID of the experiment to be deleted") @PathVariable @Experiment final String idOrLabel,
                           @ApiParam("The ID of the scan to be deleted") @PathVariable final Integer scanId,
                           @ApiParam("The removeFiles value") @RequestParam(defaultValue = "true") Boolean removeFiles,
                           @ApiParam("The file path value") @RequestParam(defaultValue = "") String filepath,
                           @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                           @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                           @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                           @ApiParam("The event  action value ") @RequestParam(defaultValue = "Deleted") String eventAction,
                           @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws Exception {
        log.debug("Controller Api- Delete scan {}", idOrLabel);
        final String experimentId = _experimentService.findExperimentIdByProjectSubjectAndIdOrLabel(getSessionUser(), projectId, subjectId, idOrLabel);
        _scanService.deleteById(getSessionUser(), experimentId, scanId, filepath, removeFiles, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    private final ExperimentService    _experimentService;
    private final ImageAssessorService _assessorService;
    private final ScanService          _scanService;
}
