package org.nrg.xapi.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.users.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import groovy.util.ResourceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Users Auth Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class UserAuthApi extends AbstractXapiProjectRestController {

	@Autowired
	public UserAuthApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final UserAuthService userAuthService) {
		super(userManagementService, roleHolder);
		_userAuthService = userAuthService;
	}
	@ApiOperation(value = "Gets the requested  user auth", notes = "Returns the  user auth with the specified user", response = String.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested user session."),
					@ApiResponse(code = 401, message = "The requested user  has not permission to access session."),
					@ApiResponse(code = 404, message = "The requested user session wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public String getUserSession(@ApiParam(value = "The ID of the csrf.") @PathVariable(required = false) final String csrf,
			@ApiParam(value = "The ID of the Xnat_csrf.") @PathVariable(required = false) final String xnatCSRF) throws NotFoundException, ResourceException  {
		log.debug("User {} requested session", getSessionUser().getUsername());
		return _userAuthService.getUserAuth(csrf, xnatCSRF).orElseThrow(() -> new NotFoundException("User Session Not Found"));
	}
	private final UserAuthService _userAuthService;
}
