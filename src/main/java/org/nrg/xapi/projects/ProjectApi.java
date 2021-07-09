package org.nrg.xapi.projects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.par.PARService;
import org.nrg.xnat.services.projects.ProjectAccessibilityService;
import org.nrg.xnat.services.projects.ProjectArchiveService;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api("XNAT project Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectApi extends AbstractXapiProjectRestController {
	
    @Autowired
    public ProjectApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProjectService projectService, final PARService parService, final ProjectArchiveService projectArchiveService, final ProjectAccessibilityService projectAccessibilityService) {
        super(userManagementService, roleHolder);
        _projectService = projectService;
        _parService = parService;
        _projectArchiveService= projectArchiveService;
        _projectAccessibilityService= projectAccessibilityService;
    }

	/**
	 * Get the requested project with the specified project ID
	 * 
	 * @param projectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public XnatProjectdata getById(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
    	log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
        return _projectService.findById(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId));
    }

	/**
	 * Get the list of all projects configures in XNAT system
	 * 
	 * @return
	 * @throws NotFoundException
	 */
    @ApiOperation(value = "Get list of projects", notes = "The projects function returns a list of all projects configured in the XNAT system.", response = XnatProjectdata.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects",produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatProjectdata> getAllProjects() throws NotFoundException  {
    	log.debug("User {} requested projects", getSessionUser().getUsername());
        return _projectService.findAll(getSessionUser());
    }

	/**
	 * Create new xnat project
	 * 
	 * @param project
	 * @param allowDataDelete
	 * @param accessibility
	 * @param xsiType
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @return
	 * @throws UserNotFoundException
	 * @throws DataFormatException
	 * @throws InsufficientPrivilegesException
	 * @throws ResourceAlreadyExistsException
	 * @throws XftItemException
	 * @throws ActionException
	 * @throws UserInitException
	 */
    @ApiOperation(value = "Create a new project", notes = "Creates the submitted project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
    			   @ApiResponse(code = 400, message = "The requested project wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 409, message = "The specified project already exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
    public XnatProjectdata createProject(@ApiParam("The project to be created.") @RequestBody final XnatProjectdata project,
    									 @ApiParam("The data allow to be delete") @RequestParam(name = "allowDataDelete", required = false) String allowDataDelete, 
    									 @ApiParam("The accessibility value ") @RequestParam(name = "accessibility", required = false)String accessibility,
    									 @ApiParam("The xsiType value ") @RequestParam(name = "xsiType", required = false)String xsiType,
    									 @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    									 @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    									 @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    									 @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
    									 @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws UserNotFoundException, DataFormatException, InsufficientPrivilegesException, ResourceAlreadyExistsException, XftItemException, ActionException, UserInitException  {
        log.debug("User {} requested to create project with ID {}", getSessionUser().getUsername(), project.getId());
        return _projectService.create(getSessionUser(), project,allowDataDelete, accessibility, xsiType,  XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
    }

   
	/**
	 * Updates the submitted project
	 * 
	 * @param projectId
	 * @param project
	 * @param filepath
	 * @param xsiType
	 * @param allowDataDelete
	 * @param accessibility
	 * @param testHyphen
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @return
	 * @throws InsufficientPrivilegesException
	 * @throws InitializationException
	 * @throws Exception
	 */
	@ApiOperation(value = "Update an existing project", notes = "Updates the submitted project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}",  consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        						produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = PUT)
    public XnatProjectdata updateProject(@ApiParam("The ID of the project to be updated") @PathVariable final String projectId,
                                         @ApiParam("The project to be updated.") @RequestBody final XnatProjectdata project,
                                         @ApiParam("The filepath value ") @RequestParam(name = "filepath", required = false)String filepath,
                                         @ApiParam("The xsiType value ") @RequestParam(name = "xsiType", required = false)String xsiType,
                                         @ApiParam("The data allow to be delete") @RequestParam(name = "allowDataDelete", required = false) String allowDataDelete, 
                                         @ApiParam("The accessibility value ") @RequestParam(name = "accessibility", required = false)String accessibility,
                                         @ApiParam("The testHyphen value ") @RequestParam(name = "testHyphen", required = false)boolean testHyphen,
                                         @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    									 @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    									 @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    									 @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
    									 @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws InsufficientPrivilegesException, InitializationException, Exception  {
        if (!StringUtils.equals(projectId, project.getId())) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the project ID is " + project.getProject() + ". These values must be the same.");
        }
        log.debug("User {} requested to update project with ID {}", getSessionUser().getUsername(), project.getId());
        return _projectService.update(getSessionUser(), project, filepath,allowDataDelete,accessibility,testHyphen,xsiType, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
    }
    
	/**
	 * Deletes the specified project
	 * 
	 * @param projectId
	 * @param removeFiles
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @throws DataFormatException
	 * @throws InitializationException
	 * @throws NotFoundException
	 */
    @ApiOperation(value = "Delete an existing project", notes = "Deletes the specified project.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified project."),
    			   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteById(@ApiParam("The ID of the project to be deleted") @PathVariable final String projectId,
    						@ApiParam("The removeFiles value ") @RequestParam(name = "removeFiles", defaultValue = "false")boolean removeFiles,
    						@ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
							@ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
							@ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
							@ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
							@ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws DataFormatException, InitializationException, NotFoundException  {
    	 log.debug("User {} requested to delete project with ID {}", getSessionUser().getUsername(), projectId);
    	 _projectService.deleteById(getSessionUser(), projectId, removeFiles, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
    }
    
    
	/**
	 * Gets the list of all requested project access request
	 * 
	 * @return
	 * @throws NotFoundException
	 */
    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the list of all project access request", response = ProjectAccessRequest.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to get projects in the ProjectAccessRequest"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/pars", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ProjectAccessRequest> getAll() throws NotFoundException {
    	log.debug("User {} requested ProjectAccessRequest ", getSessionUser().getUsername());
    	return _parService.findAll(getSessionUser());
    }
    
	/**
	 * Gets the project access request with the specified project ID
	 * 
	 * @param projectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
    			   @ApiResponse(code = 400, message = "The requested projectId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/pars", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ProjectAccessRequest> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
    	log.debug("User {} requested project with ID {} ", getSessionUser().getUsername(), projectId);
    	return _parService.findByProjectId(getSessionUser(), projectId);
    }

	/**
	 * Gets the project access request with the specified ProjectAccessRequest ID
	 * 
	 * @param parId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
    			   @ApiResponse(code = 400, message = "The requested parId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/pars/{parId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ProjectAccessRequest getByParId(@ApiParam(value = "The ID of the project access request.") @PathVariable final Integer parId) throws NotFoundException, DataFormatException {
    	log.debug("User {} requested pars with ID {} ", getSessionUser().getUsername(), parId);
    	return _parService.findByParId(getSessionUser(), parId).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, parId));
    }
    
	/**
	 * Update an existing project access request
	 * 
	 * @param parId
	 * @param accept
	 * @param decline
	 * @param projectAccessRequest
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @return
	 * @throws NotFoundException
	 */
    @ApiOperation(value = "Update an existing projectAccessRequest", notes = "Updates the submitted projectAccessRequest.", response = ProjectAccessRequest.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated projectAccessRequest."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit projectAccessRequest in the specified projectAccessRequest"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/pars/{parId}",
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = PUT)
    public ProjectAccessRequest updateProjectAccessRequest(@ApiParam("The ID of the project to be updated") @PathVariable final Integer parId,
    													   @ApiParam("The ID of the project to be updated") @RequestParam final  String accept,
    													   @ApiParam("The ID of the project to be updated") @RequestParam final  String decline,
    													   @ApiParam("The project to be updated.") @RequestBody final ProjectAccessRequest projectAccessRequest,
    													   @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    													   @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    													   @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    													   @ApiParam("The event  action value ") @RequestParam(name = "eventAction", defaultValue = "Deleted")String eventAction,
    													   @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws NotFoundException  {
    	log.debug("User {} requested to update projectAccessRequest with ID {}", getSessionUser().getUsername(), parId);
        return _parService.update(getSessionUser(), projectAccessRequest, parId, accept, decline, XnatEventUtil.getXnatEventUtil(eventType, eventReason, eventId, eventAction, eventComment));
    }
    
    
	/**
	 * Gets the requested archive project
	 * 
	 * @param projectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
    @ApiOperation(value = "Gets the requested  arc project", notes = "Returns the  arc project with the specified ID", response = ArcProject.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    			   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested arc project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/projects/{projectId}/archive_spec","/config/{projectId}/archive_spec"}, produces = MediaType.APPLICATION_XML_VALUE, method = GET)
    public ArcProject getProjectById(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException  {
        log.debug("Controller Api- get project by ID {}", projectId);
        return _projectArchiveService.findByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(ArcProject.SCHEMA_ELEMENT_NAME, projectId));
    }
    
  
	/**
	 * Gets the requested project accessibility with the specified project ID
	 * 
	 * @param projectId
	 * @param accessLevel
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	 @ApiOperation(value = "Gets the requested  project accessibility", notes = "Returns the  project accessibility with the specified project ID", response = String.class, responseContainer = "single")
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
		 			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
		 			@ApiResponse(code = 404, message = "The requested project accessibility wasn't found."),
		 			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	 @XapiRequestMapping(value = "/projects/{projectId}/accessibility/{accessLevel}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	 public String getByProjectIdAndAccessLevel(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			 @ApiParam(value = "The access level of the project.") @PathVariable final String accessLevel) throws NotFoundException, DataFormatException  {
		 log.debug("User {} requested project with ID {} and access level {}", getSessionUser().getUsername(), projectId, accessLevel);
		 return _projectAccessibilityService.findByProjectIdAndAccessLevel(getSessionUser(), projectId, accessLevel).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId));
	}
	 
	 /**
	  * Update the requested project accessibility with the specified project ID
	  * @param projectId
	  * @param access
	  * @param eventReason
	  * @param eventId
	  * @param eventType
	  * @param eventAction
	  * @param eventComment
	  * @return
	  * @throws Exception
	  */
	 @ApiOperation(value = "Update an existing project accessibility ", notes = "Updates the submitted project accessibility.", response = XnatProjectdata.class)
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project accessibility."),
		 			@ApiResponse(code = 403, message = "The user doesn't have permission to edit projects in the specified project"),
		 			@ApiResponse(code = 404, message = "The specified project doesn't exist"),
		 			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	 @XapiRequestMapping(value = "/projects/{projectId}/accessibility",
	                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        method = PUT)
	    public String updateProject(@ApiParam("The ID of the project to be updated") @PathVariable final String projectId,
	                                @ApiParam("The project access to be updated.") @RequestParam(name= "access") final String access,
	                                @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
									@ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
									@ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
									@ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
									@ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws Exception {
	        
	        log.debug("updating project accessibility with project ID {}", projectId);
	        return _projectAccessibilityService.update(getSessionUser(),access, projectId,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }
    
    private final ProjectService _projectService;
    private final PARService _parService;
    private final ProjectArchiveService _projectArchiveService;
    private final ProjectAccessibilityService _projectAccessibilityService;
}
