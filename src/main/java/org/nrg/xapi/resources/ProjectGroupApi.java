package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.subjects.XnatProjectGroup;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.files.ProjectGroupService;
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

@Api("XNAT project group Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectGroupApi extends AbstractXapiProjectRestController {
	 
	@Autowired
	public ProjectGroupApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,final ProjectGroupService projectGroupService) {
		super(userManagementService, roleHolder);
		_projectGroupService = projectGroupService;
	}

	
	@ApiOperation(value = "Get list of projects Group", notes= "The projects function returns a list of all project group configured in the XNAT system.", response = XnatProjectGroup.class, responseContainer = "list")
	@ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
       @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	@XapiRequestMapping(value = "/projects/{projectId}/groups" , produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatProjectGroup>> getProjectList(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get Resources by projectId");
		List<XnatProjectGroup> xnatProjects = _projectGroupService.findProjectGroupByProjectId(getSessionUser(), projectId);
	    if (xnatProjects == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatProjects, HttpStatus.OK);
	}
	
	private final ProjectGroupService _projectGroupService;
}
