package org.nrg.xapi.experiments;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.experiments.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT experiment Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ExperimentApi extends AbstractXapiProjectRestController {

	@Autowired
	public ExperimentApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ExperimentService experimentService) {
		super(userManagementService, roleHolder);
		_experimentService = experimentService;
	}

	@ApiOperation(value = "Gets the requested  experiment", notes = "Returns the  experiment with the specified ID", response = XnatExperimentdata.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested experiment."),
		            @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
			        @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
			        @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XnatExperimentdata getById(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested experiment with ID {} }", getSessionUser().getUsername(), experimentId);
		return _experimentService.findById(getSessionUser(), experimentId).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, experimentId));
	}

	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
		            @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
	                @ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatExperimentdata> getAllExperiments() throws NotFoundException {
		log.debug("User {} requested experiments }", getSessionUser().getUsername());
		return  _experimentService.findAll(getSessionUser()).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME));
	}
	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	               @ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	               @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
				   @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
	               @ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatExperimentdata> getAllByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws Exception {
		log.debug("User {} requested experiment with projectId {} }", getSessionUser().getUsername(), projectId);
		return _experimentService.findAllByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, projectId));
	}
	
	
	@ApiOperation(value = "Get single experiment", notes = "The experiments function returns a single experiment configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
		            @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
                    @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
	                @ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XnatExperimentdata getByIdAndProject(@ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId,
			                                    @ApiParam(value = "The ID of the project.") @PathVariable  final String projectId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested experiment with projectId {} and with ID {} }", getSessionUser().getUsername(), projectId, experimentId);
		return _experimentService.findByIdAndProjectId(getSessionUser(), experimentId, projectId).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, projectId));
	}
	
	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	               @ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	               @ApiResponse(code = 500, message = "An unexpected or unknown error occurred"),
	               @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
                   @ApiResponse(code = 404, message = "The requested experiment wasn't found.")})
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatExperimentdata> getAllByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId, 
															      @ApiParam(value = "The ID of the subject.") @PathVariable  final String subjectId) throws Exception {
		log.debug("User {} requested experiment with projectId {} and with subjectId {}}", getSessionUser().getUsername(), projectId, subjectId);
		return _experimentService.findAllByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, projectId));
	}
	
	@ApiOperation(value = "Delete an existing experiment", notes = "Deletes the specified experiment.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified experiment."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete experiments in the specified experiment"),
                   @ApiResponse(code = 404, message = "The specified experiment or experiment doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/experiments/{experimentId}","/experiments/{experimentId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteExperiment(@ApiParam("The ID of the experiment to be deleted") @PathVariable final String projectId,
    						  @ApiParam("The ID of the experiment to be deleted") @PathVariable final String experimentId,
    						  @ApiParam("The file path value") @RequestParam(name = "filepath", required = false )String filepath,
    						  @ApiParam("The removeFiles value ") @RequestParam(name = "removeFiles", defaultValue = "false")boolean removeFiles,
    						  @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    						  @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    						  @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    						  @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
    						  @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws DataFormatException, NotFoundException, org.nrg.framework.exceptions.NotFoundException  {
		log.debug("User {} delete experiment with projectId {} and with experimentId {}}", getSessionUser().getUsername(), projectId, experimentId);
        _experimentService.deleteById(getSessionUser(), experimentId, projectId, filepath,removeFiles,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
    }

	 @ApiOperation(value = "Update an existing experiment", notes = "Updates the submitted experiment.", response = XnatSubjectassessordata.class)
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated experiment."),
		 			@ApiResponse(code = 403, message = "The user doesn't have permission to edit experiment in the specified project"),
		 			@ApiResponse(code = 404, message = "The specified experiment doesn't exist"),
		 			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}"},
	                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        method = PUT)
	    public XnatExperimentdata updateExperiment(@ApiParam("The project containing the subject to be updated") @PathVariable final String projectId,
	    										   @ApiParam("The subject in which the experiment should be created") @PathVariable final String subjectId,                            
	    										   @ApiParam("The ID of the experiment to be updated") @PathVariable final String experimentId,
	    										   @ApiParam("The subject to be updated.") @RequestBody final XnatExperimentdata experiment,
	    										   @ApiParam("The data allow to be delete") @RequestParam(name = "allowDataDelete", required = false) String allowDataDelete, 
	    										   @ApiParam("The label value.")@RequestParam (name = "label", required = false)String label,
	    										   @ApiParam("The filepath value.")@RequestParam (name = "filepath",defaultValue = "")String filepath,
	    										   @ApiParam("The primary value.")@RequestParam (name = "primary", required = false)String primary,
	    										   @ApiParam("The moveAssessors value.")@RequestParam (name = "moveAssessors", required = false)String moveAssessors,
	    										   @ApiParam("The overwrite value.")@RequestParam (name = "overwrite", defaultValue = "false")boolean overwrite,
	    										   @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
	    										   @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
	    										   @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
	    										   @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
	    										   @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws DataFormatException  {
	        if (StringUtils.isNotBlank(projectId) && !StringUtils.equals(experiment.getProject(), projectId)) {
	            throw new DataFormatException("You specified the project " + projectId + " in your request but the experiment is assigned to project " + experiment.getProject() + ". These values must be the same.");
	        }
	        if (!StringUtils.equals(experimentId, experiment.getId())) {
	            throw new DataFormatException("You specified the subject ID " + experimentId + " in your request but the experiment to be updated has the ID " + experiment.getId() + ". These values must be the same.");
	        }
	        log.debug("Controller Api- Update experiment {} (ID {}) in project {}", experiment.getLabel(), experimentId, experiment.getProject());
	        return _experimentService.update(getSessionUser(), experiment, experimentId,projectId, subjectId,allowDataDelete,label, primary,moveAssessors,overwrite,filepath,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }
	 
	 
	 @ApiOperation(value = "Create a new experiment", notes = "Creates the submitted experiment.", response = XnatExperimentdata.class)
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created experiment."),
	    			   @ApiResponse(code = 403, message = "The user doesn't have permission to create experiment in the specified project"),
	    			   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
	    			   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments"},
	                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        method = POST)
	    public XnatExperimentdata createExperiment(@ApiParam("The project in which the experiment should be created") @PathVariable final String projectId,
	    										   @ApiParam("The subject in which the experiment should be created") @PathVariable final String subjectId,
	    										   @ApiParam("The subject to be created.") @RequestBody final XnatExperimentdata experiment,
	    										   @ApiParam("The xsiType value") @RequestParam(name = "xsiType", required = false )String xsiType,
	    										   @ApiParam("The allowDataDelete value") @RequestParam(name = "allowDataDelete", defaultValue = "false" )String allowDataDelete,
	    										   @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
	    										   @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
	    										   @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
	    										   @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
	    										   @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws DataFormatException, NotFoundException  {
	        final boolean experimentHasProject = StringUtils.isNotBlank(experiment.getProject());
	        final boolean hasProject        = StringUtils.isNotBlank(projectId);
	        if (!experimentHasProject && !hasProject) {
	            throw new DataFormatException("You must specify a project in which the experiment should be created.");
	        }
	        if (experimentHasProject && hasProject && !StringUtils.equals(experiment.getProject(), projectId)) {
	            throw new DataFormatException("You specified the project " + projectId + " in your request but the experiment is assigned to project " + experiment.getProject() + ". These values must be the same.");
	        }
	        if (!experimentHasProject) {
	        	experiment.setProject(projectId);
	        }
	         return _experimentService.create(getSessionUser(), experiment, projectId, subjectId,xsiType,allowDataDelete,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }
	

	private final ExperimentService _experimentService;

}
