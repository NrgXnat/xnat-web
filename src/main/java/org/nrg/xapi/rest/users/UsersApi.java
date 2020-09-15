/*
 * web: org.nrg.xapi.rest.users.UsersApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.users;

import static org.nrg.xapi.model.users.User.USER_ROW_MAPPER;
import static org.nrg.xdat.security.helpers.AccessLevel.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.framework.utilities.Patterns;
import org.nrg.xapi.authorization.UserGroupXapiAuthorization;
import org.nrg.xapi.authorization.UserResourceXapiAuthorization;
import org.nrg.xapi.exceptions.*;
import org.nrg.xapi.model.users.User;
import org.nrg.xapi.model.users.UserFactory;
import org.nrg.xapi.rest.*;
import org.nrg.xdat.om.XdatUser;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.services.PermissionsServiceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xdat.services.AliasTokenService;
import org.nrg.xdat.turbine.utils.AdminUtils;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.security.UserI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
@Api("User Management API")
@XapiRestController
@RequestMapping(value = "/users")
@Slf4j
public class UsersApi extends AbstractXapiRestController {
    @Autowired
    public UsersApi(final UserManagementServiceI userManagementService,
                    final UserFactory factory,
                    final RoleHolder roleHolder,
                    final SessionRegistry sessionRegistry,
                    final AliasTokenService aliasTokenService,
                    final PermissionsServiceI permissionsService,
                    final NamedParameterJdbcTemplate jdbcTemplate,
                    final SiteConfigPreferences siteConfig) {
        super(userManagementService, roleHolder);
        _sessionRegistry = sessionRegistry;
        _aliasTokenService = aliasTokenService;
        _permissionsService = permissionsService;
        _factory = factory;
        _jdbcTemplate = jdbcTemplate;
        _siteConfig = siteConfig;
    }

    @ApiOperation(value = "Get list of users.", notes = "The primary users function returns a list of all users of the XNAT system. This includes just the username and nothing else. You can retrieve a particular user by adding the username to the REST API URL or a list of users with abbreviated user profiles by calling /xapi/users/profiles.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of usernames."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of usernames."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authorizer)
    @AuthDelegate(UserResourceXapiAuthorization.class)
    @ResponseBody
    public List<String> usersGet() {
        return new ArrayList<>(Users.getAllLogins());
    }

    @ApiOperation(value = "Get list of user profiles.", notes = "The users' profiles function returns a list of all users of the XNAT system with brief information about each.", response = User.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of user profiles."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of users."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "profiles", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authorizer)
    @AuthDelegate(UserResourceXapiAuthorization.class)
    @ResponseBody
    public List<User> usersProfilesGet() {
        return _jdbcTemplate.query(QUERY_USER_PROFILES, USER_ROW_MAPPER);
    }

    @ApiOperation(value = "Get user profile.", notes = "The user profile function returns a user of the XNAT system with brief information.", response = User.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A user profile."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the user profile."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "profile/{username}", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authorizer)
    @AuthDelegate(UserResourceXapiAuthorization.class)
    @ResponseBody
    public User usersProfileGet(@ApiParam(value = "ID of the user to fetch", required = true) @PathVariable("username") @Username final String username) throws DataFormatException {
        if (!VALID_USERNAME.matcher(username).matches()) {
            throw new DataFormatException("The submitted username '" + username + "' is invalid.");
        }
        return _jdbcTemplate.queryForObject(QUERY_USER_PROFILE, new MapSqlParameterSource("username", username), USER_ROW_MAPPER);
    }

    @ApiOperation(value = "Get list of users who are enabled or who have interacted with the site somewhat recently.", notes = "The users' profiles function returns a list of all users of the XNAT system with brief information about each.", response = User.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of user profiles."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of usernames."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "current", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authorizer)
    @AuthDelegate(UserResourceXapiAuthorization.class)
    @ResponseBody
    public List<User> currentUsersProfilesGet() {
        return _jdbcTemplate.query(QUERY_CURRENT_USERS, new MapSqlParameterSource("maxLoginInterval", getMaxLoginInterval()).addValue("lastModifiedInterval", getLastModifiedInterval()), USER_ROW_MAPPER);
    }

    @ApiOperation(value = "Get list of active users.", notes = "Returns a map of usernames for users that have at least one currently active session, i.e. logged in or associated with a valid application session. The number of active sessions and a list of the session IDs is associated with each user.", response = Map.class, responseContainer = "Map")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of active users."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of usernames."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "active", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Admin)
    @ResponseBody
    public Map<String, Map<String, Object>> getActiveUsers() {
        final Map<String, Map<String, Object>> activeUsers = new HashMap<>();
        for (final Object principal : _sessionRegistry.getAllPrincipals()) {
            final String username;
            if (principal instanceof String) {
                username = (String) principal;
            } else if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            final List<SessionInformation> sessions = _sessionRegistry.getAllSessions(principal, false);

            // Sometimes there are no sessions, which is weird but OK, we don't want to see those entries.
            if (sessions.isEmpty()) {
                continue;
            }

            final Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("sessions", sessions.stream().map(INFO_TO_ID_FUNCTION).collect(Collectors.toList()));
            sessionData.put("count", sessions.size());
            activeUsers.put(username, sessionData);
        }
        return activeUsers;
    }

    @ApiOperation(value = "Get information about active sessions for the indicated user.", notes = "Returns a map containing a list of session IDs and usernames for users that have at least one currently active session, i.e. logged in or associated with a valid application session. This also includes the number of active sessions for each user.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of active users."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "You do not have sufficient permissions to access this user's sessions."),
                   @ApiResponse(code = 404, message = "The indicated user has no active sessions or is not a valid user."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "active/{username}", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = User)
    @ResponseBody
    public List<String> getUserActiveSessions(@ApiParam(value = "ID of the user to fetch", required = true) @PathVariable("username") @Username final String username) throws NotFoundException {
        final Object located = locatePrincipalByUsername(username);
        if (located == null) {
            throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
        }
        return _sessionRegistry.getAllSessions(located, false).stream().map(INFO_TO_ID_FUNCTION).collect(Collectors.toList());
    }

    @ApiOperation(value = "Gets the user with the specified user ID.", notes = "Returns the serialized user object with the specified user ID.", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to view this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authorizer)
    @AuthDelegate(UserResourceXapiAuthorization.class)
    public User getUser(@ApiParam(value = "Username of the user to fetch.", required = true) @PathVariable("username") @Username final String username) throws NotFoundException, InitializationException {
        return _factory.getUser(getUserI(username));
    }

    @ApiOperation(value = "Creates a new user from the request body.", notes = "Returns the newly created user object.", response = User.class)
    @ApiResponses({@ApiResponse(code = 201, message = "User successfully created."),
                   @ApiResponse(code = 400, message = "The submitted data was invalid."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to update this user."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = Admin)
    public User createUser(@RequestBody final User model) throws DataFormatException, ResourceAlreadyExistsException, InitializationException {
        validateUser(model);

        final UserI user = getUserManagementService().createUser();
        if (user == null) {
            throw new NrgServiceRuntimeException(NrgServiceError.Unknown, "Failed to create a user object for user " + model.getUsername());
        }

        user.setLogin(model.getUsername());
        user.setFirstname(model.getFirstName());
        user.setLastname(model.getLastName());
        user.setEmail(model.getEmail());
        user.setPassword(model.getPassword());
        user.setAuthorization(model.getAuthorization());

        if (model.isEnabled() != null) {
            user.setEnabled(model.isEnabled());
        }
        if (model.isVerified() != null) {
            user.setVerified(model.isVerified());
        }

        try {
            getUserManagementService().save(user, getSessionUser(), false, new EventDetails(EventUtils.CATEGORY.DATA, EventUtils.TYPE.WEB_SERVICE, Event.Added, "Requested by user " + getSessionUser().getUsername(), "Created new user " + user.getUsername() + " through XAPI user management API."));

            if (BooleanUtils.isTrue(model.isVerified()) && BooleanUtils.isTrue(model.isEnabled())) {
                //When a user is enabled and verified, send a new user email
                try {
                    AdminUtils.sendNewUserEmailMessage(user.getUsername(), user.getEmail());
                } catch (Exception e) {
                    log.error("An error occurred trying to send email to the admin: new user '{}' created with email '{}'", user.getUsername(), user.getEmail(), e);
                }
            }
            return _factory.getUser(user);
        } catch (Exception e) {
            throw new InitializationException("Error occurred creating user " + user.getLogin(), e);
        }
    }

    @ApiOperation(value = "Updates the user object with the specified username.", notes = "Returns the updated serialized user object with the specified username.", response = User.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User successfully updated."),
                   @ApiResponse(code = 304, message = "The user object was not modified because no attributes were changed."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to update this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = Admin)
    public User updateUser(@ApiParam(value = "The username of the user to create or update.", required = true) @PathVariable("username") @Username final String username, @RequestBody final User model) throws NotFoundException, InitializationException, DataFormatException, NotModifiedException {
        final UserI user = getUserI(username);
        if (StringUtils.isNotBlank(model.getUsername()) && !StringUtils.equals(user.getUsername(), model.getUsername())) {
            throw new DataFormatException("Username must match");
        }
        final boolean       isEnabled  = user.isEnabled();
        final boolean       isVerified = user.isVerified();
        final AtomicBoolean isDirty    = new AtomicBoolean();
        if (StringUtils.isNotBlank(model.getFirstName()) && !StringUtils.equals(user.getFirstname(), model.getFirstName())) {
            user.setFirstname(model.getFirstName());
            isDirty.set(true);
        }
        if (StringUtils.isNotBlank(model.getLastName()) && !StringUtils.equals(user.getLastname(), model.getLastName())) {
            user.setLastname(model.getLastName());
            isDirty.set(true);
        }
        if (StringUtils.isNotBlank(model.getEmail()) && !StringUtils.equals(user.getEmail(), model.getEmail())) {
            user.setEmail(model.getEmail());
            isDirty.set(true);
        }
        // Don't do password compare: we can't.
        if (StringUtils.isNotBlank(model.getPassword())) {
            user.setPassword(model.getPassword());
            isDirty.set(true);
        }
        if (model.getAuthorization() != null && !model.getAuthorization().equals(user.getAuthorization())) {
            user.setAuthorization(model.getAuthorization());
            isDirty.set(true);
        }
        final Boolean enabled = model.isEnabled();
        if (enabled != null && enabled != user.isEnabled()) {
            user.setEnabled(enabled);
            if (!enabled) {
                //When a user is disabled, deactivate all their AliasTokens
                try {
                    _aliasTokenService.deactivateAllTokensForUser(user.getLogin());
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            isDirty.set(true);
        }
        final Boolean verified = model.isVerified();
        if (verified != null && verified != user.isVerified()) {
            user.setVerified(verified);
            isDirty.set(true);
        }

        if (!isDirty.get()) {
            throw new NotModifiedException("No changes made to user " + username);
        }

        try {
            getUserManagementService().save(user, getSessionUser(), false, new EventDetails(EventUtils.CATEGORY.DATA, EventUtils.TYPE.WEB_SERVICE, Event.Modified, "", ""));
            if (BooleanUtils.toBooleanDefaultIfNull(model.isVerified(), false) && BooleanUtils.toBooleanDefaultIfNull(model.isEnabled(), false) && (!isEnabled || !isVerified)) {
                //When a user is enabled and verified, send a new user email
                try {
                    AdminUtils.sendNewUserEmailMessage(user.getUsername(), user.getEmail());
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            return _factory.getUser(user);
        } catch (Exception e) {
            throw new InitializationException("Error occurred modifying user '" + user.getUsername() + "'", e);
        }
    }

    @ApiOperation(value = "Invalidates all active sessions associated with the specified username.", notes = "Returns a list of session IDs that were invalidated.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "User successfully invalidated."),
                   @ApiResponse(code = 304, message = "Indicated user has no active sessions, so no action was taken."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to invalidate this user's sessions."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "active/{username}", produces = APPLICATION_JSON_VALUE, method = DELETE, restrictTo = User)
    public List<String> invalidateUser(final HttpSession current, @ApiParam(value = "The username of the user to invalidate.", required = true) @PathVariable("username") @Username final String username) throws InitializationException, NotFoundException {
        final UserI  user;
        final String currentSessionId;
        if (StringUtils.equals(getSessionUser().getUsername(), username)) {
            user = getSessionUser();
            currentSessionId = current.getId();
        } else {
            user = getUserI(username);
            currentSessionId = null;
        }
        final Object located = locatePrincipalByUsername(user.getUsername());
        if (located == null) {
            throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
        }
        return _sessionRegistry.getAllSessions(located, false)
                               .stream()
                               .map(INFO_TO_ID_INVALIDATOR_FUNCTION)
                               .filter(sessionId -> !StringUtils.equalsIgnoreCase(sessionId, currentSessionId))
                               .collect(Collectors.toList());
    }

    @ApiOperation(value = "Returns whether the user with the specified user ID is enabled.", notes = "Returns true or false based on whether the specified user is enabled or not.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User enabled status successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to get whether this user is enabled."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/enabled", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = User)
    public Boolean usersIdEnabledGet(@ApiParam(value = "The ID of the user to retrieve the enabled status for.", required = true) @PathVariable("username") @Username final String username) throws NotFoundException, InitializationException {
        try {
            final UserI user = getUserManagementService().getUser(username);
            if (user == null) {
                throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
            }
            return user.isEnabled();
        } catch (UserInitException e) {
            throw new InitializationException("An error occurred initializing the user '" + username + "'", e);
        } catch (UserNotFoundException e) {
            throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
        }
    }

    @ApiOperation(value = "Sets the user's enabled state.", notes = "Sets the enabled state of the user with the specified user ID to the value of the flag parameter.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User enabled status successfully set."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to enable or disable this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/enabled/{flag}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = Admin)
    public Boolean usersIdEnabledFlagPut(@ApiParam(value = "ID of the user to fetch", required = true) @PathVariable("username") @Username final String username, @ApiParam(value = "The value to set for the enabled status.", required = true) @PathVariable("flag") Boolean flag) throws NotModifiedException, InitializationException, NotFoundException {
        enableAndVerifyUser(username, flag, null);
        return true;
    }

    @ApiOperation(value = "Returns whether the user with the specified user ID is verified.", notes = "Returns true or false based on whether the specified user is verified or not.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User verified status successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to view this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/verified", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = User)
    public Boolean usersIdVerifiedGet(@ApiParam(value = "The ID of the user to retrieve the verified status for.", required = true) @PathVariable("username") @Username final String username) throws NotFoundException, InitializationException {
        try {
            final UserI user = getUserManagementService().getUser(username);
            if (user == null) {
                throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
            }
            return user.isVerified();
        } catch (UserInitException e) {
            throw new InitializationException("An error occurred initializing the user " + username);
        } catch (UserNotFoundException e) {
            throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
        }
    }

    @ApiOperation(value = "Sets the user's verified state.", notes = "Sets the verified state of the user with the specified user ID to the value of the flag parameter.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User verified status successfully set."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to verify or un-verify this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/verified/{flag}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = Admin)
    public Boolean usersIdVerifiedFlagPut(@ApiParam(value = "ID of the user to fetch", required = true) @PathVariable("username") @Username final String username, @ApiParam(value = "The value to set for the verified status.", required = true) @PathVariable("flag") Boolean flag) throws NotFoundException, InitializationException, NotModifiedException {
        enableAndVerifyUser(username, null, flag);
        return true;
    }

    @ApiOperation(value = "Returns the roles for the user with the specified user ID.", notes = "Returns a collection of the user's roles.", response = Collection.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User roles successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to view this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/roles", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = User)
    public Collection<String> usersIdRolesGet(@ApiParam(value = "The ID of the user to retrieve the roles for.", required = true) @PathVariable("username") @Username final String username) {
        return getUserRoles(username);
    }

    @ApiOperation(value = "Adds one or more roles to a user.", notes = "Assigns one or more new roles to a user.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "All specified user roles successfully added."),
                   @ApiResponse(code = 202, message = "Some user roles successfully added, but some may have failed. Check the return value for roles that the service was unable to add."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to add roles to this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/roles", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = Admin)
    public Collection<String> usersIdAddRoles(@ApiParam(value = "ID of the user to add a role to", required = true) @PathVariable("username") @Username final String username,
                                              @ApiParam(value = "The user's new roles.", required = true) @RequestBody final List<String> roles) throws NotFoundException, InitializationException, PartialFailureException {
        final UserI              user   = getUserI(username);
        final Collection<String> failed = new ArrayList<>();
        for (final String role : roles) {
            try {
                getRoleHolder().addRole(getSessionUser(), user, role);
            } catch (Exception e) {
                failed.add(role);
                log.error("Error occurred adding role " + role + " to user " + username + ".", e);
            }
        }
        if (failed.isEmpty()) {
            Collections.emptyList();
        }
        throw new PartialFailureException("The following roles were not added to user " + username + ": " + StringUtils.join(failed, ", "));
    }

    @ApiOperation(value = "Adds a role to a user.", notes = "Assigns a new role to a user.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User role successfully added."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to add a role to this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/roles/{role}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = Admin)
    public Boolean usersIdAddRole(@ApiParam(value = "ID of the user to add a role to", required = true) @PathVariable("username") @Username final String username,
                                  @ApiParam(value = "The user's new role.", required = true) @PathVariable("role") final String role) throws PartialFailureException, NotFoundException, InitializationException {
        return usersIdAddRoles(username, Collections.singletonList(role)).isEmpty();
    }

    @ApiOperation(value = "Removes one or more roles from a user.", notes = "Removes one or more new roles from a user.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "All specified user roles successfully removed."),
                   @ApiResponse(code = 202, message = "Some user roles successfully removed, but some may have failed. Check the return value for roles that the service was unable to remove."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to remove roles from this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/roles", produces = APPLICATION_JSON_VALUE, method = DELETE, restrictTo = Admin)
    public Collection<String> usersIdRemoveRoles(@ApiParam(value = "ID of the user to remove role from", required = true) @PathVariable("username") @Username final String username,
                                                 @ApiParam(value = "The roles to be removed.", required = true) @RequestBody final List<String> roles) throws NotFoundException, InitializationException, PartialFailureException {
        final UserI              user   = getUserI(username);
        final Collection<String> failed = new ArrayList<>();
        for (final String role : roles) {
            try {
                getRoleHolder().deleteRole(getSessionUser(), user, role);
            } catch (Exception e) {
                failed.add(role);
                log.error("Error occurred remove role " + role + " from user " + user.getLogin() + ".", e);
            }
        }
        if (failed.isEmpty()) {
            Collections.emptyList();
        }
        throw new PartialFailureException("The following roles were not removed from user " + username + ": " + StringUtils.join(failed, ", "));
    }

    @ApiOperation(value = "Remove a user's role.", notes = "Removes a user's role.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User role successfully removed."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to remove a role from this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/roles/{role}", produces = APPLICATION_JSON_VALUE, method = DELETE, restrictTo = Admin)
    public Boolean usersIdRemoveRole(@ApiParam(value = "ID of the user to delete a role from", required = true) @PathVariable("username") @Username final String username,
                                     @ApiParam(value = "The user role to delete.", required = true) @PathVariable("role") String role) throws PartialFailureException, NotFoundException, InitializationException {
        return usersIdRemoveRoles(username, Collections.singletonList(role)).isEmpty();
    }

    @ApiOperation(value = "Returns the groups for the user with the specified user ID.", notes = "Returns a collection of the user's groups.", response = Set.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User groups successfully retrieved."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to get the groups for this user."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/groups", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = User)
    public Set<String> usersIdGroupsGet(@ApiParam(value = "The ID of the user to retrieve the groups for.", required = true) @PathVariable("username") @Username final String username) throws NotFoundException, InitializationException {
        return Groups.getGroupsForUser(getUserI(username)).keySet();
    }

    @ApiOperation(value = "Adds the user to one or more groups.", notes = "Assigns the user to one or more new groups.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "User successfully added for all specified groups."),
                   @ApiResponse(code = 202, message = "User was successfully added to some of the specified groups, but some may have failed. Check the return value for groups that the service was unable to add."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to add this user to groups."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/groups", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = Authorizer)
    @AuthDelegate(UserGroupXapiAuthorization.class)
    public Collection<String> usersIdAddGroups(@ApiParam(value = "ID of the user to add to the specified groups", required = true) @PathVariable("username") @Username final String username,
                                               @ApiParam(value = "The groups to which the user should be added.", required = true) @UserGroup @RequestBody final List<String> groups) throws NotFoundException, InitializationException, PartialFailureException {
        final UserI              user   = getUserI(username);
        final Collection<String> failed = new ArrayList<>();
        for (final String group : groups) {
            try {
                Groups.addUserToGroup(group, user, getSessionUser(), EventUtils.ADMIN_EVENT(getSessionUser()));
            } catch (Exception e) {
                failed.add(group);
                log.error("Error occurred adding user " + user.getLogin() + " to group " + group + ".", e);
            }
        }
        if (failed.isEmpty()) {
            return Collections.emptyList();
        }
        throw new PartialFailureException("The following groups were not added to user " + username + ": " + StringUtils.join(failed, ", "));
    }

    @ApiOperation(value = "Removes the user from one or more groups.", notes = "Removes the user from one or more groups.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "User successfully removed from all specified groups."),
                   @ApiResponse(code = 202, message = "User was successfully removed from some of the specified groups, but some may have failed. Check the return value for groups that the service was unable to remove."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to remove this user from groups."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/groups", produces = APPLICATION_JSON_VALUE, method = DELETE, restrictTo = User)
    public Collection<String> usersIdRemoveGroups(@ApiParam(value = "ID of the user to remove role from", required = true) @PathVariable("username") @Username final String username,
                                                  @ApiParam(value = "The groups from which the user should be removed.", required = true) @RequestBody final List<String> groups) throws NotFoundException, InitializationException, PartialFailureException {
        final UserI              user   = getUserI(username);
        final Collection<String> failed = new ArrayList<>();
        for (final String group : groups) {
            try {
                Groups.removeUserFromGroup(user, getSessionUser(), group, EventUtils.ADMIN_EVENT(getSessionUser()));
            } catch (Exception e) {
                failed.add(group);
                log.error("Error occurred adding user " + user.getLogin() + " to group " + group + ".", e);
            }
        }
        if (failed.isEmpty()) {
            return Collections.emptyList();
        }
        throw new PartialFailureException("The following groups were not removed from user " + username + ": " + StringUtils.join(failed, ", "));
    }

    @ApiOperation(value = "Adds a user to a group.", notes = "Assigns user to a group.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User successfully added to group."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to assign this user to groups."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/groups/{group}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = Authorizer)
    @AuthDelegate(UserGroupXapiAuthorization.class)
    public Boolean usersIdAddGroup(@ApiParam(value = "ID of the user to add to a group", required = true) @PathVariable("username") @Username final String username, @ApiParam(value = "The user's new group.", required = true) @UserGroup @PathVariable("group") final String group) throws PartialFailureException, NotFoundException, InitializationException {
        return usersIdAddGroups(username, Collections.singletonList(group)).isEmpty();
    }

    @ApiOperation(value = "Removes a user from a group.", notes = "Removes a user from a group.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "User's group successfully removed."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "Not authorized to remove this user from groups."),
                   @ApiResponse(code = 404, message = "User not found."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "{username}/groups/{group}", produces = APPLICATION_JSON_VALUE, method = DELETE, restrictTo = User)
    public Boolean usersIdRemoveGroup(@ApiParam(value = "ID of the user to remove from group", required = true) @PathVariable("username") @Username final String username, @ApiParam(value = "The group to remove the user from.", required = true) @PathVariable("group") final String group) throws PartialFailureException, NotFoundException, InitializationException {
        return usersIdRemoveGroups(username, Collections.singletonList(group)).isEmpty();
    }

    @ApiOperation(value = "Returns list of projects that user has edit access.", notes = "Returns list of projects that user has edit access.", response = String.class, responseContainer = "List")
    @XapiRequestMapping(value = "projects", produces = APPLICATION_JSON_VALUE, method = GET)
    public List<String> getProjectsByUser() {
        return _permissionsService.getUserEditableProjects(getSessionUser());
    }

    public static class Event {
        public static String Added                 = "Added User";
        public static String Disabled              = "Disabled User";
        public static String Enabled               = "Enabled User";
        public static String DisabledForInactivity = "Disabled User Due To Inactivity";
        public static String Modified              = "Modified User";
        public static String ModifiedEmail         = "Modified User Email";
        public static String ModifiedPassword      = "Modified User Password";
        public static String ModifiedPermissions   = "Modified User Permissions";
        public static String ModifiedSettings      = "Modified User Settings";
        public static String VerifiedEmail         = "Verified User Email";
    }

    private UserI getUserI(final String username) throws NotFoundException, InitializationException {
        try {
            final UserI user = getUserManagementService().getUser(username);
            if (user == null) {
                throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
            }
            return user;
        } catch (UserInitException e) {
            throw new InitializationException("An error occurred initializing the user " + username, e);
        } catch (UserNotFoundException e) {
            throw new NotFoundException(XdatUser.SCHEMA_ELEMENT_NAME, username);
        }
    }

    @Nullable
    private Object locatePrincipalByUsername(final String username) {
        Object located = null;
        for (final Object principal : _sessionRegistry.getAllPrincipals()) {
            if (principal instanceof String && username.equals(principal)) {
                located = principal;
                break;
            } else if (principal instanceof UserDetails && username.equals(((UserDetails) principal).getUsername())) {
                located = principal;
                break;
            } else if (username.equals(principal.toString())) {
                located = principal;
                break;
            }
        }
        return located;
    }

    private void enableAndVerifyUser(final String username, final Boolean enable, final Boolean verify) throws NotFoundException, InitializationException, NotModifiedException {
        if (enable == null && verify == null) {
            return;
        }
        final boolean isEnable          = enable != null;
        final boolean isEnableAndVerify = enable != null && verify != null;
        final boolean isVerify          = verify != null;

        final UserI user = getUserI(username);
        if (isEnable) {
            final boolean isEnabled = user.isEnabled();
            if (enable == isEnabled) {
                throw new NotModifiedException("User " + username + " is already " + (enable ? "enabled" : "disabled"));
            }
            user.setEnabled(enable);
        }
        if (isVerify) {
            final boolean isVerified = user.isVerified();
            if (verify == isVerified) {
                throw new NotModifiedException("User " + username + " is already " + (verify ? "verified" : "unverified"));
            }
            user.setVerified(verify);
        }
        try {
            getUserManagementService().save(user, getSessionUser(), false, new EventDetails(EventUtils.CATEGORY.DATA, EventUtils.TYPE.WEB_SERVICE, (isEnable ? Event.Enabled : Event.Disabled) + (isEnableAndVerify ? " and " : "") + (isVerify ? Event.VerifiedEmail : "Unverified Email"), "", ""));
            if (user.isEnabled() && user.isVerified()) {
                // Send a new user email when a user is enabled and verified
                AdminUtils.sendNewUserEmailMessage(username, user.getEmail());
            }
        } catch (UserNotFoundException e) {
            throw new NotFoundException(username);
        } catch (Exception e) {
            throw new InitializationException("Error occurred enabling/disabling/verifying/unverifying user " + username);
        }
    }

    private void validateUser(final User model) throws DataFormatException, ResourceAlreadyExistsException, InitializationException {
        final DataFormatException exception = new DataFormatException();
        exception.validateBlankAndRegex("username", model.getUsername(), Patterns.USERNAME);
        exception.validateBlankAndRegex("email", model.getEmail(), Patterns.EMAIL);
        exception.validateBlankAndRegex("firstName", model.getFirstName(), Patterns.LIMIT_XSS_CHARS);
        exception.validateBlankAndRegex("lastName", model.getLastName(), Patterns.LIMIT_XSS_CHARS);
        if (exception.hasDataFormatErrors()) {
            throw exception;
        }

        final String username = model.getUsername();
        try {
            getUserI(username);
            // If we make it here, the user was found so that's an error.
            throw new ResourceAlreadyExistsException("user", username);
        } catch (NotFoundException e) {
            // This is actually what we want.
        }
    }

    private int getLastModifiedInterval() {
        int interval = _siteConfig.getSecurityLastModifiedInterval();
        return (interval > 0) ? interval : 1; // Make sure its greater than 0
    }

    private int getMaxLoginInterval() {
        int interval = _siteConfig.getSecurityMaxLoginInterval();
        return (interval > 0) ? interval : 1; // Make sure its greater than 0
    }

    private static class SessionInfoToIdFunction implements Function<SessionInformation, String> {
        SessionInfoToIdFunction(final boolean invalidate) {
            _invalidate = invalidate;
        }

        @Override
        public String apply(final SessionInformation sessionInformation) {
            if (_invalidate) {
                sessionInformation.expireNow();
            }
            return sessionInformation.getSessionId();
        }

        private final boolean _invalidate;
    }

    private static final String                  QUERY_USER_PROFILES             = "SELECT enabled, login AS username, xdat_user_id AS id, firstname AS firstName, lastname AS lastName, email, verified, last_modified, auth.max_login AS lastSuccessfulLogin FROM xdat_user JOIN xdat_user_meta_data ON xdat_user.user_info=xdat_user_meta_data.meta_data_id JOIN (SELECT xdat_username, max(last_successful_login) max_login FROM xhbm_xdat_user_auth GROUP BY xdat_username) auth ON xdat_user.login=auth.xdat_username ORDER BY xdat_user.xdat_user_id";
    private static final String                  QUERY_CURRENT_USERS             = "SELECT enabled, login AS username, xdat_user_id AS id, firstname AS firstName, lastname AS lastName, email, verified, last_modified, auth.max_login AS lastSuccessfulLogin FROM xdat_user JOIN xdat_user_meta_data ON xdat_user.user_info=xdat_user_meta_data.meta_data_id JOIN (SELECT xdat_username, max(last_successful_login) max_login FROM xhbm_xdat_user_auth GROUP BY xdat_username) auth ON xdat_user.login=auth.xdat_username WHERE (xdat_user.enabled=1 OR (max_login > (CURRENT_DATE - (INTERVAL '1 year' * :maxLoginInterval)) OR (max_login IS NULL AND (xdat_user_meta_data.last_modified > (CURRENT_DATE - (INTERVAL '1 year' * :lastModifiedInterval)) ) ) )) ORDER BY xdat_user.xdat_user_id";
    private static final String                  QUERY_USER_PROFILE              = "SELECT enabled, login AS username, xdat_user_id AS id, firstname AS firstName, lastname AS lastName, email, verified, last_modified, auth.max_login AS lastSuccessfulLogin FROM xdat_user JOIN xdat_user_meta_data ON xdat_user.user_info=xdat_user_meta_data.meta_data_id JOIN (SELECT xdat_username, max(last_successful_login) max_login FROM xhbm_xdat_user_auth GROUP BY xdat_username) auth ON xdat_user.login=auth.xdat_username WHERE xdat_user.login=:username";
    private static final Pattern                 VALID_USERNAME                  = Pattern.compile("^[a-zA-Z0-9]+[a-zA-Z0-9._-]*$");
    private static final SessionInfoToIdFunction INFO_TO_ID_FUNCTION             = new SessionInfoToIdFunction(false);
    private static final SessionInfoToIdFunction INFO_TO_ID_INVALIDATOR_FUNCTION = new SessionInfoToIdFunction(true);

    private final SessionRegistry            _sessionRegistry;
    private final AliasTokenService          _aliasTokenService;
    private final PermissionsServiceI        _permissionsService;
    private final UserFactory                _factory;
    private final NamedParameterJdbcTemplate _jdbcTemplate;
    private final SiteConfigPreferences      _siteConfig;
}
