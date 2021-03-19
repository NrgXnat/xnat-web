package org.nrg.xapi.projects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.projects.ProjectAccessibilityService;
import org.nrg.xnat.services.projects.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@Api("XNAT project Accessibility Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectAccessibilityApi extends AbstractXapiProjectRestController {
	
    @Autowired
    public ProjectAccessibilityApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProjectAccessibilityService projectAccessibilityService) {
        super(userManagementService, roleHolder);
        _projectAccessibilityService = projectAccessibilityService;
    }

	
	 @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = String.class, responseContainer = "single")
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	 @ApiResponse(code = 404, message = "The requested project wasn't found."),
	 @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	 @XapiRequestMapping(value = "/projects/{projectId}/accessibility", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	 public ResponseEntity<String> getProjectAccessbilityByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get project by ID {}", projectId);
		String xnatProject = _projectAccessibilityService.findByProjectId(getSessionUser(), projectId);
		if (xnatProject == null) {
			throw new NotFoundException("No Project with ID " + projectId + " was found.");
		}
		return new ResponseEntity<>(xnatProject, HttpStatus.OK);
	}
	 
	 @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = String.class, responseContainer = "single")
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	 @ApiResponse(code = 404, message = "The requested project wasn't found."),
	 @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	 @XapiRequestMapping(value = "/projects/{projectId}/accessibility/{accessLevel}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	 public ResponseEntity<String> getProjectAccessbilityByProjectIdAndAccessLevel(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			 @ApiParam(value = "The access level of the project.") @PathVariable(required = false) final String accessLevel) throws Exception {
		log.debug("Controller Api- get project by ID {}", projectId);
		String xnatProject = _projectAccessibilityService.findByProjectIdAndAccessLevel(getSessionUser(), projectId, accessLevel);
		if (xnatProject == null) {
			throw new NotFoundException("No Project with ID " + projectId + " was found.");
		}
		return new ResponseEntity<>(xnatProject, HttpStatus.OK);
	}
	 
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
	                                @ApiParam("The project access to be updated.") @RequestParam(name= "access") final String access) throws Exception {
	        
	        log.debug("Controller Api- Update project {}", projectId);
	        return _projectAccessibilityService.update(getSessionUser(),access, projectId);
	    }
	 
	 private ProjectAccessibilityService _projectAccessibilityService;
}
