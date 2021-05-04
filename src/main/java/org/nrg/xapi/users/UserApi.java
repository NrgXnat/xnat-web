package org.nrg.xapi.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Users Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class UserApi extends AbstractXapiProjectRestController {

	@Autowired
	public UserApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final UserService userService) {
		super(userManagementService, roleHolder);
		_userService = userService;
	}
	
	@ApiOperation(value = "Gets the requested  users", notes = "Returns the  users with the specified projectId", response = XdatUsergroup.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested users."),
					@ApiResponse(code = 400, message = "The requested projectId  wasn't found."),
					@ApiResponse(code = 404, message = "The requested users wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/users", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XdatUsergroup> getByProject(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested users with project ID {}", getSessionUser().getUsername(), projectId);
		return _userService.findByProject(getSessionUser(), projectId);
		
	}
	
	
	@ApiOperation(value = "Gets the requested  users", notes = "Returns the  users with the specified projectId", response = XdatUsergroup.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested user groups."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested user groups wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/groups", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XdatUsergroup> getUserGroupByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested user group with project ID {}", getSessionUser().getUsername(), projectId);
		return _userService.findUserGroupByProject(getSessionUser(), projectId);
	}
	
	
	@ApiOperation(value = "Gets the requested  users", notes = "Returns the  users with the specified projectId and GroupId", response = XdatUsergroup.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested uers."),
					@ApiResponse(code = 400, message = "The requested either projectId or groupId wasn't found."),
					@ApiResponse(code = 404, message = "The requested uers wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/groups/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XdatUsergroup getUserGroupByGroupIdAndProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
																		 @ApiParam(value = "The ID of the group.") @PathVariable final String groupId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested user group with project ID {} and with group ID {}", getSessionUser().getUsername(), projectId, groupId);
		return _userService.findUserGroupByGroupIdAndProject(getSessionUser(),groupId, projectId).orElseThrow(() -> new NotFoundException(XdatUsergroup.SCHEMA_ELEMENT_NAME, groupId));
	}
	
	private final UserService _userService;

}
