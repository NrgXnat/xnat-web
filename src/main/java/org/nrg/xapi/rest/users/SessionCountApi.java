package org.nrg.xapi.rest.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.extensions.SessionCountService;
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

@Api("XNAT Session Count Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class SessionCountApi extends AbstractXapiProjectRestController {

	@Autowired
    public SessionCountApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final SessionCountService  sessionCountService) {
        super(userManagementService, roleHolder);
        _sessionCountService = sessionCountService;
    }
	
	@ApiOperation(value = "Gets the Ip Whitelist", notes = "Returns the  IpWhitelist", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested IpWhitelist wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/services/sessions", "/services/sessions/{username}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public Integer getSessionCount(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String username) throws NotFoundException, DataFormatException, InsufficientPrivilegesException, InitializationException {
		log.debug("User {} requested IpWhitelist", getSessionUser().getUsername());
		return _sessionCountService.findSessionCount(getSessionUser(), username);
	}
	
	private final SessionCountService _sessionCountService;

}
