package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpServletRequest;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ProjectGroupResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Api("XNAT Project Group File Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectGroupResourceXapi extends AbstractXapiProjectRestController {
	private static final Logger _log = LoggerFactory.getLogger(ProjectGroupResourceXapi.class);

	@Autowired
	public ProjectGroupResourceXapi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final ProjectGroupResourceService groupResourceService) {
		super(userManagementService, roleHolder);
		_groupResourceService = groupResourceService;
	}

	@ApiOperation(value = "Get the resources for a experiment or resource", notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources or /projects/XNAT_01/subjects/XNAT_01_01/resources.", response = XnatAbstractresourceI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
			@ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = { /* "/projects/{projectId}/groups", */
			"/projects/{projectId}/groups/{groupId}"}, produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = GET)
	public ResponseEntity<String> getProjectGroupResources(HttpServletRequest request,
			@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the group.") @PathVariable(required = false) final String groupId)
			throws Exception {
		if (request != null) {
			_log.debug("Controller getProjectGroupResources  - Request content {} " + request);
		}
		_log.debug("Controller getProjectGroupResources by project id and group id  response ");
		final String projectGroup = _groupResourceService.getProjectGroupResources(projectId, groupId);
		if (projectGroup == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(projectGroup, HttpStatus.OK);
	}

	private final ProjectGroupResourceService _groupResourceService;
}
