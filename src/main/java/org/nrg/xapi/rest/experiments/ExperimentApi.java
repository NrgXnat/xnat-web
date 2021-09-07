package org.nrg.xapi.rest.experiments;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatScscandata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.experiments.ImageAssessorService;
import org.nrg.xnat.services.experiments.ExperimentService;
import org.nrg.xnat.services.scans.ScanService;
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
	public ExperimentApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ExperimentService experimentService, final ImageAssessorService assessorService,
			  final ScanService scanService) {
		super(userManagementService, roleHolder);
		_experimentService = experimentService;
		_assessorService = assessorService;
		_scanService = scanService;
	}

	/**
	 * Get the experiment with the specified experiment ID
	 * 
	 * @param experimentId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
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

	/**
	 * Get list of experiments
	 * 
	 * @return
	 * @throws NotFoundException
	 */
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
		            @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
	                @ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatExperimentdata> getAllExperiments() throws NotFoundException {
		log.debug("User {} requested experiments }", getSessionUser().getUsername());
		return  _experimentService.findAll(getSessionUser());
	}
	
	
	/**
	 * Get the list of experiments with the specified project ID
	 * 
	 * @param projectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	               @ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	               @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
				   @ApiResponse(code = 404, message = "The requested experiment wasn't found."),
	               @ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatExperimentdata> getAllByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested experiment with projectId {} }", getSessionUser().getUsername(), projectId);
		return _experimentService.findAllByProjectId(getSessionUser(), projectId);
	}
	
	
	/**
	 * Get the experiment with the specified project ID and experiment ID
	 * 
	 * @param experimentId
	 * @param projectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
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
	
	
	/**
	 * Get the list of experiments with the specified project ID and subject ID
	 * 
	 * @param projectId
	 * @param subjectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	               @ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	               @ApiResponse(code = 500, message = "An unexpected or unknown error occurred"),
	               @ApiResponse(code = 400, message = "The requested experimentIds wasn't found."),
                   @ApiResponse(code = 404, message = "The requested experiment wasn't found.")})
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatExperimentdata> getAllByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId, 
															      @ApiParam(value = "The ID of the subject.") @PathVariable  final String subjectId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested experiment with projectId {} and with subjectId {}}", getSessionUser().getUsername(), projectId, subjectId);
		return _experimentService.findAllByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId);
	}
	
	
	/**
	 * Delete an existing experiment
	 * 
	 * @param projectId
	 * @param experimentId
	 * @param filepath
	 * @param removeFiles
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @throws DataFormatException
	 * @throws NotFoundException
	 * @throws org.nrg.framework.exceptions.NotFoundException
	 */
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

	
	/**
	 * Update an existing experiment
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param experiment
	 * @param allowDataDelete
	 * @param label
	 * @param filepath
	 * @param primary
	 * @param moveAssessors
	 * @param overwrite
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @param fixScanTypes
	 * @param pullDataFromHeaders
	 * @param triggerPipelines
	 * @param supressEmail
	 * @return
	 * @throws DataFormatException
	 */
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
	    										   @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment,
	    										   @ApiParam("The fixScanTypes value ") @RequestParam(name = "fixScanTypes", defaultValue = "false",required = false)boolean fixScanTypes,
	    										   @ApiParam("The pullDataFromHeaders value ") @RequestParam(name = "pullDataFromHeaders", defaultValue = "false", required = false)boolean pullDataFromHeaders,
	    										   @ApiParam("The trigger Pipelines value ") @RequestParam(name = "triggerPipelines", defaultValue = "false", required = false) boolean triggerPipelines,
	    										   @ApiParam("The supress Email value ") @RequestParam(name = "supressEmail",defaultValue = "false", required = false) boolean supressEmail) throws DataFormatException  {
	        if (StringUtils.isNotBlank(projectId) && !StringUtils.equals(experiment.getProject(), projectId)) {
	            throw new DataFormatException("You specified the project " + projectId + " in your request but the experiment is assigned to project " + experiment.getProject() + ". These values must be the same.");
	        }
	        if (!StringUtils.equals(experimentId, experiment.getId())) {
	            throw new DataFormatException("You specified the subject ID " + experimentId + " in your request but the experiment to be updated has the ID " + experiment.getId() + ". These values must be the same.");
	        }
	        log.debug("Controller Api- Update experiment {} (ID {}) in project {}", experiment.getLabel(), experimentId, experiment.getProject());
	        return _experimentService.update(getSessionUser(), experiment, experimentId,projectId, subjectId,allowDataDelete,label, primary,moveAssessors,overwrite,filepath,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment),
	        		fixScanTypes, pullDataFromHeaders, triggerPipelines, supressEmail);
	    }
	 
	 
	/**
	 * Create a new experiment
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experiment
	 * @param xsiType
	 * @param allowDataDelete
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @param triggerPipelines
	 * @param supressEmail
	 * @return
	 * @throws DataFormatException
	 * @throws NotFoundException
	 */
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
	    										   @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment,
	    										   @ApiParam("The trigger Pipelines value ") @RequestParam(name = "triggerPipelines", defaultValue = "false", required = false) boolean triggerPipelines,
	    										   @ApiParam("The supress Email value ") @RequestParam(name = "supressEmail",defaultValue = "false", required = false) boolean supressEmail) throws DataFormatException, NotFoundException  {
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
	         return _experimentService.create(getSessionUser(), experiment, projectId, subjectId,xsiType,allowDataDelete,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ), triggerPipelines, supressEmail);
	    }
	 
	 
	/**
	 * Get list of experiments with specified project ID , subject ID and experiment
	 * ID
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	 @ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
						@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId  wasn't found."),
						@ApiResponse(code = 404, message = "The requested assessors wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public List<XnatImageassessordata> getAllByProjectIdAndSubjectIdAndExperimentId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
				@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
				@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException {
			log.debug("User {} requested assessor with project ID {} , with subject ID {} and with experiment ID {} }", getSessionUser().getUsername(), projectId, subjectId, experimentId);
			return _assessorService.findAllByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId,experimentId );
		}
		
	/**
	 * Get list of experiments with specified project ID , subject ID, experiment ID
	 * and assessor ID
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param assessorId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
		@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a experiment configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns the currently configured experiment."),
						@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or assessorId  wasn't found."),
						@ApiResponse(code = 404, message = "The requested assessor wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public XnatImageassessordata getByIdAndProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
				@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
				@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
				@ApiParam(value = "The ID of the assessor.") @PathVariable final String assessorId) throws NotFoundException, DataFormatException  {
			log.debug("User {} requested assessor with project ID {} , with subject ID {} , with experiment ID {} and with assessor ID {}  }", getSessionUser().getUsername(), projectId, subjectId, experimentId, assessorId);
			return _assessorService.findByIdAndProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId,experimentId, assessorId).orElseThrow(() -> new NotFoundException(XnatImageassessordata.SCHEMA_ELEMENT_NAME, assessorId));
		}
		
		
	/**
	 * Get list of assessors with specified experiment ID
	 * 
	 * @param experimentId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
		@ApiOperation(value = "Get list of assessors", notes = "The experiments function returns a list of all assessors configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured assessors."),
						@ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
						@ApiResponse(code = 404, message = "The requested assessors wasn't found."),		
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public List<XnatImageassessordata> getAllByExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException  {
			log.debug("User {} requested assessor with experiment ID {} }", getSessionUser().getUsername(), experimentId);
			return _assessorService.findAllByExperimentId(getSessionUser(), experimentId);
		}
		
		
	/**
	 * Get the assessors with specified experiment ID and assessor ID
	 * 
	 * @param assessorId
	 * @param experimentId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
		@ApiOperation(value = "Get list of assessors", notes = "The experiments function returns a assessor configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured assessors."),
						@ApiResponse(code = 400, message = "The requested either assessorId or experimentId wasn't found."),
						@ApiResponse(code = 404, message = "The requested assessor wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public XnatImageassessordata getByAssessorIdAndExperimentId(@ApiParam(value = "The ID of the assessor.") @PathVariable final String assessorId,
				@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException  {
			log.debug("User {} requested assessor with experiment ID {} and with assessor ID {}  }", getSessionUser().getUsername(), experimentId, assessorId);
			return _assessorService.findByIdAndExperimentId(getSessionUser(), assessorId, experimentId).orElseThrow(() -> new NotFoundException(XnatImageassessordata.SCHEMA_ELEMENT_NAME, experimentId));
		}
		
		
	/**
	 * Gets the Scan Quality Lable
	 * 
	 * @param projectId
	 * @return
	 * @throws InitializationException
	 */
		@ApiOperation(value = "Gets the Scan Quality Lable", notes = "Returns the  Scan Quality Lable", response = String.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested Scan Quality Lable wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = {"/services/scan-quality-labels", "/services/scan-quality-labels/{projectId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	    public String getAllScanQualityLable(@ApiParam("The ID of the project to be updated") @PathVariable(required = false) final String projectId) throws InitializationException  {
			log.debug("User {} requested Scan Quality Lable", getSessionUser().getUsername());
			return _scanService.findAllScanQualityLable(getSessionUser(), projectId);
		}
	
		
	/**
	 * Get list of scans with specified scan ID
	 * 
	 * @param projectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
		@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatImagescandata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
						@ApiResponse(code = 400, message = "The requested projectId  wasn't found."),
						@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/projects/{projectId}/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public  List<XnatImagescandata> getScanTypesByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId) throws NotFoundException, DataFormatException  {
			log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
			return _scanService.findAllScanTypesByProjectId(getSessionUser(), projectId);
		}
		
		
	/**
	 * Get list of scan types
	 * 
	 * @return
	 * @throws NotFoundException
	 */
		@ApiOperation(value = "Get list of scan types", notes = "The scans function returns a list of all scan types configured in the XNAT system.", response = XnatImagescandata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
						@ApiResponse(code = 404, message = "The requested scan types wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public List<XnatImagescandata> getAllScanTypes() throws NotFoundException {
			log.debug("User {} requested scan types", getSessionUser().getUsername());
			return _scanService.findAllScanTypes(getSessionUser());
		}
		
		
	/**
	 * Get the list of scans with specified assessed ID
	 * 
	 * @param assessedId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
		@ApiOperation(value = "Get scans with specified assessed ID", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
						@ApiResponse(code = 400, message = "The requested assessedId  wasn't found."),
						@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/experiments/{assessedId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public List<XnatImagescandata> getAllByAssessedId(@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId) throws NotFoundException, DataFormatException {
			log.debug("User {} requested assessor with ID {}", getSessionUser().getUsername(), assessedId);
			return _scanService.findAllByAssessedId(getSessionUser(), assessedId);
		}
		
		
		/**
		 * Get the scan with specified assessor ID
		 * @param assessedId
		 * @param scanId
		 * @return
		 * @throws NotFoundException
		 * @throws DataFormatException
		 */
		@ApiOperation(value = "Get the scan with specified assessor ID", notes = "The scans function returns a scan configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "Single")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
						@ApiResponse(code = 400, message = "The requested either assessedId or scanId wasn't found."),
						@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public XnatImagescandata getByAssessedIdAndScanId(@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
				@ApiParam(value = "The ID of the scan.") @PathVariable  final Integer scanId) throws NotFoundException, DataFormatException  {
			log.debug("User {} requested assessor with ID {} and scan with ID {}", getSessionUser().getUsername(), assessedId, scanId);
			return _scanService.findByAssessedIdAndScanId(getSessionUser(), assessedId, scanId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, assessedId));
		}
		
		
	/**
	 * Get list of scans with specified project ID , subject ID, experiment ID
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
		@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
						@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId wasn't found."),
						@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public List<XnatImagescandata> getScansByProjectAndSubjectAndExperiment(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
				@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
				@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException{
			log.debug("User {} requested project with ID {}, subject with ID {} and experiment with ID {}", getSessionUser().getUsername(), projectId, subjectId, experimentId);
			return _scanService.findAllByProjectIdAndSubjectIdAndExperimentId(getSessionUser(),projectId,subjectId , experimentId);
		}
		
		
	/**
	 * Get scan of specified scan ID
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param scanId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
		@ApiOperation(value = "Get scan of specified scanId", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "Single")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
						@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or scanId wasn't found."),
						@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public XnatImagescandata getByProjectIdAndSubjectIdAndExperimentIdAndScanId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
				@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
				@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
				@ApiParam(value = "The ID of the scan.") @PathVariable final Integer scanId) throws NotFoundException, DataFormatException  {
			log.debug("User {} requested project with ID {}, subject with ID {}, experiment with ID {} and scan with ID {}", getSessionUser().getUsername(), projectId, subjectId, experimentId, scanId);
			return _scanService.findByProjectIdAndSubjectIdAndExperimentIdAndScanId(getSessionUser(),projectId,subjectId , experimentId, scanId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, experimentId));
		}
		
		
	/**
	 * Get All scanners
	 * 
	 * @param scanTable
	 * @param projectId
	 * @return
	 * @throws InsufficientPrivilegesException
	 */
		@ApiOperation(value = "Get All scanner", notes = "The scannners function returns a list of all scanner configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
		@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
						@ApiResponse(code = 400, message = "The requested scanner  wasn't found."),
						@ApiResponse(code = 404, message = "The requested scanner wasn't found."),			
						@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
		@XapiRequestMapping(value = "/scanners", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
		public List<Map<String, String>> getAllScanners(@ApiParam(value = "The value of the scanTable.") @RequestParam(required = false) final String scanTable,
														@ApiParam(value = "The ID of the project.") @RequestParam(required = false) final String projectId) throws InsufficientPrivilegesException{
			log.debug("User {} requested scanners", getSessionUser().getUsername());
			return _scanService.findAllScanners(getSessionUser(), scanTable, projectId);
		}
		
		
	/**
	 * Delete an existing scan
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param assessedId
	 * @param scanId
	 * @param removeFiles
	 * @param filepath
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @throws Exception
	 */
		@ApiOperation(value = "Delete an existing scan", notes = "Deletes the specified scan.")
	    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified scan."),
	                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified scan"),
	                   @ApiResponse(code = 404, message = "The specified scan or project doesn't exist"),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}",
	    							 "/experiments/{assessedId}/scans/{scanId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
	    public void deleteScan(@ApiParam("The ID of the project to be deleted") @PathVariable(required = false) final String projectId,
	    						  @ApiParam("The ID of the subject to be deleted") @PathVariable(required = false) final String subjectId,
	    						  @ApiParam("The ID of the experiment to be deleted") @PathVariable final String assessedId,
	    						  @ApiParam("The ID of the scan to be deleted") @PathVariable final Integer scanId,
	    						  @ApiParam("The removeFiles value") @RequestParam(name = "removeFiles", defaultValue = "true" )Boolean removeFiles,
	    						  @ApiParam("The file path value") @RequestParam(name = "filepath", defaultValue = "" )String filepath,
	    						  @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
								  @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
								  @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
								  @ApiParam("The event  action value ") @RequestParam(name = "eventAction", defaultValue = "Deleted")String eventAction,
								  @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws Exception {
	        log.debug("Controller Api- Delete scan {}", assessedId);
	        _scanService.deleteById(getSessionUser(), assessedId, scanId, filepath,removeFiles, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }

	private final ExperimentService _experimentService;
	private final ImageAssessorService _assessorService;
	private final ScanService _scanService;

}
