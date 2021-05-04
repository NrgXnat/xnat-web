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
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.projects.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api("XNAT project Resource Management API")
@XapiRestController
@ResponseBody
@RequestMapping("/projects")
@Slf4j
public class ProjectApi extends AbstractXapiProjectRestController {
    @Autowired
    public ProjectApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProjectService projectService) {
        super(userManagementService, roleHolder);
        _projectService = projectService;
    }

    @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public XnatProjectdata getById(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
    	log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
        return _projectService.findById(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId));
    }

    @ApiOperation(value = "Get list of projects", notes = "The projects function returns a list of all projects configured in the XNAT system.", response = XnatProjectdata.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatProjectdata> getAllProjects() throws NotFoundException  {
    	log.debug("User {} requested projects", getSessionUser().getUsername());
        return _projectService.findAll(getSessionUser());
    }

    @ApiOperation(value = "Create a new project", notes = "Creates the submitted project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
    			   @ApiResponse(code = 400, message = "The requested project wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 409, message = "The specified project already exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
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

   

	@ApiOperation(value = "Update an existing project", notes = "Updates the submitted project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "{projectId}",  consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
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
    
    @ApiOperation(value = "Delete an existing project", notes = "Deletes the specified project.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified project."),
    			   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
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
    
    private final ProjectService _projectService;
}
