package org.nrg.xapi.rest.users;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.model.users.User;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xnat.services.extensions.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT user setting Management API")
@XapiRestController
@ResponseBody
@RequestMapping("/user")
@Slf4j
public class UserSettingsApi extends AbstractXapiProjectRestController {
	
	@Autowired
    public UserSettingsApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final UserSettingsService userSettingsService) {
        super(userManagementService, roleHolder);
        _userSettingsService = userSettingsService;
    }
	
	@ApiOperation(value = "Gets the user list", notes = "Returns the  user list", response = String.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested user list."),
    	           @ApiResponse(code = 400, message = "The requested user list wasn't found."),
                   @ApiResponse(code = 404, message = "The requested user list wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<String> getAllUser() throws InitializationException, InsufficientPrivilegesException  {
		log.debug("User {} requested IpWhitelist", getSessionUser().getUsername());
		return _userSettingsService.findAllUser(getSessionUser());
	}
	
	
	@ApiOperation(value = "Gets the user details", notes = "Returns the  user details", response = String.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested user details."),
    	           @ApiResponse(code = 400, message = "The requested user details wasn't found."),
                   @ApiResponse(code = 404, message = "The requested user details wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "{userId}",produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public User getUserByUserId(@ApiParam("The ID of the user") @PathVariable final String userId) throws InitializationException, InsufficientPrivilegesException, UserNotFoundException, UserInitException {
		log.debug("User {} requested IpWhitelist", getSessionUser().getUsername());
		return _userSettingsService.findUserByUserId(getSessionUser(), userId);
	}
	
	@ApiOperation(value = "update existing Study Routing", notes = " updating the existing Study Routing", response = void.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Study Routing."),
    	           @ApiResponse(code = 400, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	 @XapiRequestMapping(value = {"actions/{action}","actions/{userId}/{action}"}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
     																produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = PUT)
    public void updateStudyRouting(@ApiParam("The ID of the studyInstanceUid to be updated") @PathVariable final String action,
    		@ApiParam(value = "The ID of the project to be updated.") @PathVariable(required = false) final String userId) throws InitializationException, DataFormatException  {
		log.debug("User {} requested Study Routing", getSessionUser().getUsername());
		_userSettingsService.updateUserAction(action, userId);
	}
	
	@ApiOperation(value = "delete Study Routing", notes = " delete the Study Routing with specified instance Uid", response = void.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Study Routing."),
    	           @ApiResponse(code = 400, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	 @XapiRequestMapping(value = {"actions/{action}","actions/{userId}/{action}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteStudyRouting(@ApiParam("The ID of the studyInstanceUid to be updated") @PathVariable final String action,
    		@ApiParam("The ID of the studyInstanceUid to be updated") @PathVariable(required = false) final String userId) throws InitializationException, DataFormatException{
		log.debug("User {} requested Study Routing", getSessionUser().getUsername());
		_userSettingsService.deleteUserAction(action, userId);
	}
	
	private final UserSettingsService _userSettingsService;

}
