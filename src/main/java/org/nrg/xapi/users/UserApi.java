package org.nrg.xapi.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.users.UserService;
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
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested uers."),
	@ApiResponse(code = 404, message = "The requested uers wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/users", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XdatUsergroup>> getUserByProject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get Users by projectId");
		List<XdatUsergroup> xnatSubject = _userService.findByProject(getSessionUser(), projectId);
		if (xnatSubject == null) {
			throw new NotFoundException("No project with ID was found.");
		}
		return new ResponseEntity<>(xnatSubject, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  users", notes = "Returns the  users with the specified projectId", response = XdatUsergroup.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested uers."),
	@ApiResponse(code = 404, message = "The requested uers wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/groups", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XdatUsergroup>> getUserGroupByProject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get Users by projectId");
		List<XdatUsergroup> xdatUsergroups = _userService.getUserGroupByProject(getSessionUser(), projectId);
		if (xdatUsergroups == null) {
			throw new NotFoundException("No project with ID was found.");
		}
		return new ResponseEntity<>(xdatUsergroups, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  users", notes = "Returns the  users with the specified projectId and GroupId", response = XdatUsergroup.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested uers."),
	@ApiResponse(code = 404, message = "The requested uers wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/groups/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XdatUsergroup> getUserGroupByGroupIdAndProject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
																		 @ApiParam(value = "The ID of the group.") @PathVariable(required = false) final String groupId) throws Exception {
		log.debug("Controller Api- get Users by projectId and GroupId");
		XdatUsergroup xdatUsergroup = _userService.getUserGroupByGroupIdAndProject(getSessionUser(),groupId, projectId);
		if (xdatUsergroup == null) {
			throw new NotFoundException("No project with ID was found.");
		}
		return new ResponseEntity<>(xdatUsergroup, HttpStatus.OK);
	}
	
	private final UserService _userService;

}
