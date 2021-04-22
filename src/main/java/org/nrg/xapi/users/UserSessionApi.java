package org.nrg.xapi.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpSession;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.users.UserSessionService;
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

@Api("XNAT Users Session Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class UserSessionApi extends AbstractXapiProjectRestController {

	@Autowired
	public UserSessionApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final UserSessionService userSessionService) {
		super(userManagementService, roleHolder);
		_userSessionService = userSessionService;
	}
	
	@ApiOperation(value = "Gets the requested  user session", notes = "Returns the  user session with the specified user", response = String.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested user session."),
					@ApiResponse(code = 404, message = "The requested user session wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/JSESSION", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public String getUserSession(HttpSession httpSession,@ApiParam(value = "The ID of the csrf.") @PathVariable(required = false) final String csrf) throws NotFoundException  {
		log.debug("User {} requested user Session ", getSessionUser().getUsername());
		return _userSessionService.findJsession(getSessionUser(),httpSession, csrf).orElseThrow(() -> new NotFoundException("Jsession was not found"));
	}
	private final UserSessionService _userSessionService;
}
