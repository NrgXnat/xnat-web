package org.nrg.xapi.projects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.projects.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api("XNAT project Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectApi extends AbstractXapiProjectRestController {
    @Autowired
    public ProjectApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProjectService projectService) {
        super(userManagementService, roleHolder);
        _projectService = projectService;
    }

    @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<XnatProjectdata> getProjectById(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
        log.debug("Controller Api- get project by ID {}", projectId);
        XnatProjectdata xnatProject = _projectService.findById(getSessionUser(), projectId);
        if (xnatProject == null) {
            throw new NotFoundException("No Project with ID " + projectId + " was found.");
        }
        return new ResponseEntity<>(xnatProject, HttpStatus.OK);
    }

    @ApiOperation(value = "Get list of projects", notes = "The projects function returns a list of all projects configured in the XNAT system.", response = XnatProjectdata.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<List<XnatProjectdata>> getAllProjectList() throws Exception {
        log.debug("Controller Api- get projects");
        List<XnatProjectdata> xnatProjects = _projectService.getAll(getSessionUser());
        if (xnatProjects == null) {
            throw new NotFoundException("No Project with XnatProjectdata was found.");
        }
        return new ResponseEntity<>(xnatProjects, HttpStatus.OK);
    }

    @ApiOperation(value = "Create a new project", notes = "Creates the submitted project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects",
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST)
    public XnatProjectdata createProject(@ApiParam("The project to be created.") @RequestBody final XnatProjectdata project) throws Exception {
        log.debug("Controller Api- Create project: {}", project);
        return _projectService.create(getSessionUser(), project);
    }

    @ApiOperation(value = "Update an existing project", notes = "Updates the submitted project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}",
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST)
    public XnatProjectdata updateProject(@ApiParam("The ID of the project to be updated") @PathVariable final String projectId,
                                         @ApiParam("The project to be updated.") @RequestBody final XnatProjectdata project) throws Exception {
        if (!StringUtils.equals(projectId, project.getId())) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the project ID is " + project.getProject() + ". These values must be the same.");
        }
        log.debug("Controller Api- Update project {}", projectId);
        return _projectService.update(getSessionUser(), project);
    }

    @ApiOperation(value = "Delete an existing project", notes = "Deletes the specified project.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteProject(@ApiParam("The ID of the project to be deleted") @PathVariable(required = false) final String projectId) throws Exception {
        log.debug("Controller Api- Delete project {}", projectId);
        _projectService.deleteById(getSessionUser(), projectId);
    }

    private final ProjectService _projectService;
}
