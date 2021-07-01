package org.nrg.xnat.services.extensions.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.UserRolesService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserRolesServiceImpl implements UserRolesService {

	@Override
	public Set<String> findAll(UserI authUser, String userId) throws UserNotFoundException, UserInitException, DataFormatException, NotFoundException, InsufficientPrivilegesException {
		if (!Roles.isSiteAdmin(authUser)) {
            throw new InsufficientPrivilegesException("User does not have privileges to access this project.");
        }
		Set<String> result = new HashSet<>();
		if (StringUtils.isBlank(userId)) {
		 throw new DataFormatException("UserId wasn't found" + userId);
		}
		UserI user = getUser(userId);
		if (Objects.isNull(user)) {
		throw new NotFoundException("user wasn't found");
		}
		for (final String role : Roles.getRoles(user)) {
			result.add(role);
		}
		return result;
	}

	private UserI getUser(String userId) throws UserNotFoundException, UserInitException {
		return Users.getUser(userId);
	}

	@Override
	public Set<String> createRoles(UserI authUser, String userId,List<String> reqRoles) throws UserNotFoundException, UserInitException, InitializationException, InsufficientPrivilegesException {
		if (!Roles.isSiteAdmin(authUser)) {
            throw new InsufficientPrivilegesException("User does not have privileges to access this project.");
        } 
		UserI user = getUser(userId);
		 final Set<String> roles = new HashSet<>();
		if (reqRoles.size() > 0) {
			Collections.addAll(roles,reqRoles.toString().split(","));
		}
		  try {
			  final Set<String> existing = new HashSet<>(Roles.getRoles(user));
	            // If existing and submitted are the same, there's nothing to do.
	            if (roles.equals(existing)) {
	               return roles;
	            }
	            final Sets.SetView<String> added   = Sets.difference(roles, existing);
	            final Sets.SetView<String> deleted = Sets.difference(existing, roles);
	           deleteRoles(deleted, authUser, user, userId);
	            //add roles and save one at a time so that there is a separate workflow entry for each one
	          addAndSaveRole(added, authUser, user, userId);
	          return added;
		  }catch (Exception e) {
			  throw new InitializationException(e.getMessage());
		}
	}

	private void addAndSaveRole(SetView<String> added, UserI authUser, UserI user, String userId) throws Exception {
		 for (final String role : added) {
             //add role if isn't there
             if (Roles.addRole(authUser, user, role)) {
                 log.debug("Added role {} to user {}", role, userId);
             } else {
                 log.warn("Tried to add role {} to user {}, but that didn't happen for some reason", role, userId);
             }
             user = Users.getUser(userId);//get fresh db copy
         }
		
	}

	private void deleteRoles(SetView<String> deleted, UserI authUser, UserI user, String userId) throws Exception {
		 for (final String role : deleted) {
             if (Roles.deleteRole(authUser, user, role)) {
                 log.debug("Deleted role {} from user {}", role, userId);
             } else {
                 log.warn("Tried to delete role {} from user {}, but that didn't happen for some reason", role, userId);
             }
             user = Users.getUser(userId); //get fresh db copy
         }
		
	}

}
