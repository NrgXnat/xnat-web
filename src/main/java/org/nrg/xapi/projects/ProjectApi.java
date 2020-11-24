package org.nrg.xapi.projects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
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
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

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
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested subject."),
	@ApiResponse(code = 404, message = "The requested project wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatProjectdata> getSubjectBySubjectId(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get project by projectId");
		XnatProjectdata xnatProject = _projectService.findById(getSessionUser(), projectId);
		if (xnatProject == null) {
			throw new NotFoundException("No Subject with ID was found.");
		}
		return new ResponseEntity<>(xnatProject, HttpStatus.OK);
	}

	@ApiOperation(value = "Get list of projects", notes = "The projects function returns a list of all projects configured in the XNAT system.", response = XnatProjectdata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatProjectdata>> getAllExperimentList() throws Exception {
		log.debug("Controller Api- get projects");
		List<XnatProjectdata> xnatProjects = _projectService.getAll(getSessionUser());
		if (xnatProjects == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatProjects, HttpStatus.OK);
	}
	
	private final ProjectService _projectService;
}
