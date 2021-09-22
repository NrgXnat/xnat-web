package org.nrg.xapi.rest.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Set;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.RoleServiceI;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT user roles Management API")
@XapiRestController
@ResponseBody
@RequestMapping("/user")
@Slf4j
public class UserRolesApi extends AbstractXapiProjectRestController {
	
	@Autowired
    public UserRolesApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final RoleServiceI roleService) {
        super(userManagementService, roleHolder);
        _roleService = roleService;
    }
	
	@ApiOperation(value = "Gets the user roles", notes = "Returns the  user roles", response = String.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested user roles."),
    	           @ApiResponse(code = 400, message = "The requested user roles wasn't found."),
                   @ApiResponse(code = 404, message = "The requested user roles wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/{userId}/roles", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public Set<String> getAllStudyRouting(@ApiParam("The ID the user ") @PathVariable final String userId) throws UserNotFoundException, DataFormatException, NotFoundException, InsufficientPrivilegesException, UserInitException  {
		log.debug("User {} requested user roles", getSessionUser().getUsername());
		return _roleService.findAll(getSessionUser(), userId);
	}
	
	@ApiOperation(value = "create user roles", notes = " creating the user roles", response = void.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Study Routing."),
    	           @ApiResponse(code = 400, message = "The requested user roles wasn't found."),
                   @ApiResponse(code = 404, message = "The requested user roles wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	 @XapiRequestMapping(value = "/{userId}/roles", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
     																produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
    public void createRoles(@ApiParam("The ID the user ") @PathVariable final String userId,
    		@ApiParam("The value of role user ") @RequestParam final List<String> roles) throws UserNotFoundException, InitializationException, InsufficientPrivilegesException, UserInitException{
		log.debug("User {} requested user roles", getSessionUser().getUsername());
		_roleService.createRoles(getSessionUser(),userId, roles);
	}
	
	private final RoleServiceI _roleService;
}
