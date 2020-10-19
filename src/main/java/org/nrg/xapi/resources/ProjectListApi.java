package org.nrg.xapi.resources;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.subjects.XnatProject;
import org.nrg.xapi.model.subjects.XnatSubject;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ProjectListService;
import org.nrg.xnat.services.resources.SubjectListService;
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
public class ProjectListApi extends AbstractXapiProjectRestController {
	 
	@Autowired
	public ProjectListApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final ProjectListService projectListService) {
		super(userManagementService, roleHolder);
		_projectListService = projectListService;
	}
	
	@ApiOperation(value = "Gets the requested project", notes= "Returns the project with the specified ID", response = XnatProject.class, responseContainer = "List")
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
        @ApiResponse(code = 404, message = "The requested project wasn't found."),
        @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	@XapiRequestMapping(value = "/projects/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatProject> getProjectById(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get Resources by projectId");
		XnatProject xnatProject = _projectListService.findById(getSessionUser(),projectId);
	    if (xnatProject == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatProject, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of projects", notes= "The projects function returns a list of all subjects configured in the XNAT system.", response = XnatProject.class, responseContainer = "single")
	@ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
       @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	@XapiRequestMapping(value = "/projects" , produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatProject>> getProjectList() throws Exception {
		log.debug("Controller Api- get Resources by projectId");
		List<XnatProject> xnatProjects = _projectListService.getAll(getSessionUser());
	    if (xnatProjects == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatProjects, HttpStatus.OK);
	}
	
	private final ProjectListService _projectListService;

}
