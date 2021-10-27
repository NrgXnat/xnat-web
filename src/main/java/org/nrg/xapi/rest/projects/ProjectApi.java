package org.nrg.xapi.rest.projects;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.authorization.CreateProjectXapiAuthorization;
import org.nrg.xapi.authorization.ProjectAccessRequestXapiAuthorization;
import org.nrg.xapi.exceptions.*;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.AuthDelegate;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.ArcProjectI;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.par.PARService;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api("XNAT project Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectApi extends AbstractXapiProjectRestController {
    @Autowired
    public ProjectApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProjectService projectService, final PARService parService) {
        super(userManagementService, roleHolder);
        _projectService = projectService;
        _parService = parService;
    }

    @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = XnatProjectdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatProjectdataI getById(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
        return _projectService.findById(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId));
    }

    @ApiOperation(value = "Get list of projects", notes = "The projects function returns a list of all projects configured in the XNAT system.", response = XnatProjectdataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatProjectdataI> getAllProjects() throws NotFoundException {
        log.debug("User {} requested projects", getSessionUser().getUsername());
        return _projectService.findAll(getSessionUser());
    }

    @ApiOperation(value = "Create a new project", notes = "Creates the submitted project.", response = XnatProjectdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
                   @ApiResponse(code = 400, message = "The requested project wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 409, message = "The specified project already exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST, restrictTo = AccessLevel.Authorizer)
    @AuthDelegate(CreateProjectXapiAuthorization.class)
    public XnatProjectdataI createProject(@ApiParam("The project to be created.") @RequestBody final XnatProjectdataI project,
                                          @ApiParam("The data allow to be delete") @RequestParam(defaultValue = "false") boolean allowDataDeletion,
                                          @ApiParam("The accessibility value ") @RequestParam(required = false) String accessibility,
                                          @ApiParam("The xsiType value ") @RequestParam(required = false) String xsiType,
                                          @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                          @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                          @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                          @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                                          @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws UserNotFoundException, DataFormatException, InsufficientPrivilegesException, ResourceAlreadyExistsException, XftItemException, ActionException, UserInitException {
        log.debug("User {} requested to create project with ID {}", getSessionUser().getUsername(), project.getId());
        return _projectService.create(getSessionUser(), project, allowDataDeletion, accessibility, xsiType, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    @ApiOperation(value = "Update an existing project", notes = "Updates the submitted project.", response = XnatProjectdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = PUT, restrictTo = AccessLevel.Edit)
    public XnatProjectdataI updateProject(@ApiParam("The ID of the project to be updated") @PathVariable @Project final String projectId,
                                          @ApiParam("The project to be updated.") @RequestBody final XnatProjectdataI project,
                                          @ApiParam("The filepath value ") @RequestParam(required = false) String filepath,
                                          @ApiParam("The xsiType value ") @RequestParam(required = false) String xsiType,
                                          @ApiParam("The data allow to be delete") @RequestParam(defaultValue = "false") boolean allowDataDeletion,
                                          @ApiParam("The accessibility value ") @RequestParam(required = false) String accessibility,
                                          @ApiParam("The testHyphen value ") @RequestParam(required = false) boolean testHyphen,
                                          @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                          @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                          @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                          @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                                          @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws Exception {
        if (!StringUtils.equals(projectId, project.getId())) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the project ID is " + project.getId() + ". These values must be the same.");
        }
        log.debug("User {} requested to update project with ID {}", getSessionUser().getUsername(), project.getId());
        return _projectService.update(getSessionUser(), project, filepath, allowDataDeletion, accessibility, testHyphen, xsiType, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    @ApiOperation(value = "Delete an existing project", notes = "Deletes the specified project.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE, restrictTo = AccessLevel.Delete)
    public void deleteById(@ApiParam("The ID of the project to be deleted") @PathVariable @Project final String projectId,
                           @ApiParam("The removeFiles value ") @RequestParam(defaultValue = "false") boolean removeFiles,
                           @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                           @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                           @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                           @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                           @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws DataFormatException, InitializationException, NotFoundException {
        log.debug("User {} requested to delete project with ID {}", getSessionUser().getUsername(), projectId);
        _projectService.deleteById(getSessionUser(), projectId, removeFiles, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    @ApiOperation(value = "Gets a list of open project access requests", notes = "Returns the list of all project access request", response = ProjectAccessRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to get projects in the ProjectAccessRequest"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/pars", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ProjectAccessRequest> getAll() throws NotFoundException {
        log.debug("User {} requested a list of open project access requests", getSessionUser().getUsername());
        return _parService.findAll(getSessionUser());
    }

    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
                   @ApiResponse(code = 400, message = "The requested projectId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/pars", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ProjectAccessRequest> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {} ", getSessionUser().getUsername(), projectId);
        return _parService.findByProjectId(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
                   @ApiResponse(code = 400, message = "The requested parId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/pars/{parId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Authorizer)
    @AuthDelegate(ProjectAccessRequestXapiAuthorization.class)
    public ProjectAccessRequest getByParId(@ApiParam(value = "The ID of the project access request.") @PathVariable final int parId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested pars with ID {} ", getSessionUser().getUsername(), parId);
        return _parService.findByParId(getSessionUser(), parId).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, parId));
    }

    @ApiOperation(value = "Update an existing project access request", notes = "Updates the submitted project access request.", response = ProjectAccessRequest.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project access request."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit access requests in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/pars/{parId}",
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = PUT, restrictTo = AccessLevel.Authorizer)
    @AuthDelegate(ProjectAccessRequestXapiAuthorization.class)
    public ProjectAccessRequest updateProjectAccessRequest(@ApiParam("The ID of the project to be updated") @PathVariable final int parId,
                                                           @ApiParam("The ID of the project to be updated") @RequestParam final String accept,
                                                           @ApiParam("The ID of the project to be updated") @RequestParam final String decline,
                                                           @ApiParam("The project to be updated.") @RequestBody final ProjectAccessRequest projectAccessRequest,
                                                           @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                                           @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                                           @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                                           @ApiParam("The event  action value ") @RequestParam(defaultValue = "Deleted") String eventAction,
                                                           @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws NotFoundException {
        log.debug("User {} requested to update projectAccessRequest with ID {}", getSessionUser().getUsername(), parId);
        return _parService.update(getSessionUser(), projectAccessRequest, parId, accept, decline, XnatEventUtil.getXnatEventUtil(eventType, eventReason, eventId, eventAction, eventComment));
    }

    @ApiOperation(value = "Gets the requested arc project", notes = "Returns the arc project for the project with the specified ID", response = ArcProjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested arc project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/projects/{projectId}/archive_spec", "/config/{projectId}/archive_spec"}, produces = MediaType.APPLICATION_XML_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public ArcProjectI getProjectById(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws NotFoundException, DataFormatException {
        log.debug("Controller Api- get project by ID {}", projectId);
        return _projectService.findArcProjectByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(ArcProject.SCHEMA_ELEMENT_NAME, projectId));
    }

    @ApiOperation(value = "Gets the requested  project accessibility", notes = "Returns the  project accessibility with the specified project ID", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested project accessibility wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/accessibility/{accessLevel}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public String getByProjectIdAndAccessLevel(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                               @ApiParam(value = "The access level of the project.") @PathVariable final String accessLevel) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {} and access level {}", getSessionUser().getUsername(), projectId, accessLevel);
        return _projectService.findByProjectIdAndAccessLevel(getSessionUser(), projectId, accessLevel).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId));
    }

    @ApiOperation(value = "Update an existing project accessibility ", notes = "Updates the submitted project accessibility.", response = XnatProjectdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project accessibility."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/accessibility",
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = PUT, restrictTo = AccessLevel.Edit)
    public String updateProject(@ApiParam("The ID of the project to be updated") @PathVariable @Project final String projectId,
                                @ApiParam("The project access to be updated.") @RequestParam() final String access,
                                @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                                @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws Exception {
        log.debug("User {} updating project {} accessibility to {}", getSessionUser().getUsername(), projectId, access);
        return _projectService.update(getSessionUser(), access, projectId, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    private final ProjectService _projectService;
    private final PARService     _parService;
}
