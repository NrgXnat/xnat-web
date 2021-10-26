package org.nrg.xapi.rest.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XdatUsergroupI;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.db.FavEntries;
import org.nrg.xnat.services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
	
	
	@ApiOperation(value = "Gets the requested  project groups", notes = "Returns the  project groups with the specified projectId and GroupId", response = XdatUsergroupI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested uers."),
					@ApiResponse(code = 400, message = "The requested either projectId or groupId wasn't found."),
					@ApiResponse(code = 404, message = "The requested uers wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/groups/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XdatUsergroupI getUserGroupByGroupIdAndProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
															@ApiParam(value = "The ID of the group.") @PathVariable final String groupId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested user group with project ID {} and with group ID {}", getSessionUser().getUsername(), projectId, groupId);
		return _userService.findUserGroupByGroupIdAndProject(getSessionUser(),groupId, projectId).orElseThrow(() -> new NotFoundException(XdatUsergroup.SCHEMA_ELEMENT_NAME, groupId));
	}
	
	
	@ApiOperation(value = "Gets the requested  user Favorite projects", notes = "Returns the  user Favorite project with the specified datatype", response = XdatUsergroupI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested user groups."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested user Favorite project wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/users/favorites/{dataType}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<FavEntries> getAllUserFavorites(@ApiParam(value = "The vlaue of dataType.") @PathVariable final String dataType) throws NotFoundException, DataFormatException {
		log.debug("User {} requested user fav with dataType {}", getSessionUser().getUsername(), dataType);
		return _userService.FindAllUserFavorites(getSessionUser(), dataType);
	}
	
	@ApiOperation(value = "Gets the requested  user Favorite project", notes = "Returns the  user Favorite project with the specified projectId", response = XdatUsergroupI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested user groups."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested user groups wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/users/favorites/{dataType}/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public FavEntries getUserFavorites(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The vlaue of dataType.") @PathVariable final String dataType) throws NotFoundException, DataFormatException {
		log.debug("User {} requested user Favorite project with project ID {} and with data type {}", getSessionUser().getUsername(), projectId, dataType);
		return _userService.findUserFavorite(getSessionUser(), projectId, dataType).orElseThrow(() -> new NotFoundException("FavEntries wasn't found for Project ID "+ projectId));
	}
	
	
	@ApiOperation(value = "update the requested  user Favorite project", notes = "Returns the  users with the specified projectId", response = XdatUsergroupI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested user groups."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested user Favorite project wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/users/favorites/{dataType}/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = PUT)
	public List<FavEntries> updateUserFavorites(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The vlaue of dataType.") @PathVariable final String dataType) throws NotFoundException, DataFormatException {
		log.debug("User {} updated user fav with project ID {} and with data type ()", getSessionUser().getUsername(), projectId, dataType);
		return _userService.updateUserFavorite(getSessionUser(), projectId, dataType);
	}
	
	
	@ApiOperation(value = "delete the requested  user Favorite project", notes = "delete the  user Favorite project with the specified projectId", response = XdatUsergroupI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested user groups."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested user Favorite project wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/users/favorites/{dataType}/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = DELETE)
	public void deleteUserFavorites(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The vlaue of dataType.") @PathVariable final String dataType) throws NotFoundException, DataFormatException {
		log.debug("User {} requested user fav with project ID {}", getSessionUser().getUsername(), dataType);
		 _userService.deleteUserFavorite(getSessionUser(), projectId, dataType);
	}
	
	@ApiOperation(value = "delete the requested  project groups", notes = "Returns the  project groups with the specified projectId and GroupId", response = XdatUsergroupI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested uers."),
					@ApiResponse(code = 400, message = "The requested either projectId or groupId wasn't found."),
					@ApiResponse(code = 404, message = "The requested project wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/groups/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE, method = DELETE)
	public void deleteUserGroupByGroupIdAndProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
															 @ApiParam(value = "The ID of the group.") @PathVariable final String groupId,
															 @ApiParam(value = "The displayName of the group.") @RequestParam final String displayName) throws DataFormatException, NotFoundException   {
		log.debug("User {} requested user group with project ID {} and with group ID {}", getSessionUser().getUsername(), projectId, groupId);
		 _userService.deleteByGroupIdAndProject(getSessionUser(), groupId, projectId, displayName);
	}
	
	
	@ApiOperation(value = "update the requested  project groups", notes = "Returns the  project groups with the specified projectId and GroupId", response = XdatUsergroupI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested uers."),
					@ApiResponse(code = 400, message = "The requested either projectId or groupId wasn't found."),
					@ApiResponse(code = 404, message = "The requested project wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/groups/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE, method = PUT)
	public void updateUserGroupByGroupIdAndProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
															 @ApiParam(value = "The ID of the group.") @PathVariable final String groupId,
															 @ApiParam(value = "The XdatUserGroup request.") @RequestBody final XdatUsergroup group) throws DataFormatException, NotFoundException, InitializationException   {
		log.debug("User {} requested user group with project ID {} and with group ID {}", getSessionUser().getUsername(), projectId, groupId);
		
		//TODO - Need to add this map data from request 
		Map<String, Object> groupProperties = new HashMap<>();
		
		 _userService.updateByGroupIdAndProject(getSessionUser(), group, groupId, projectId, groupProperties);
	}
	
	
	@ApiOperation(value = "Gets the Ip Whitelist", notes = "Returns the  IpWhitelist", response = XnatProjectdataI.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested IpWhitelist wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/services/sessions", "/services/sessions/{username}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public Integer getSessionCount(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String username) throws NotFoundException, DataFormatException, InsufficientPrivilegesException, InitializationException {
		log.debug("User {} requested IpWhitelist", getSessionUser().getUsername());
		return _userService.findSessionCount(getSessionUser(), username);
	}
	
	private final UserService _userService;

}
