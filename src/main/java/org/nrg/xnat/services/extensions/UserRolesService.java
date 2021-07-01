package org.nrg.xnat.services.extensions;

import java.util.List;
import java.util.Set;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;

public interface UserRolesService {

	Set<String> findAll(UserI authUser,String userId) throws UserNotFoundException, UserInitException, DataFormatException, NotFoundException, InsufficientPrivilegesException;

	Set<String> createRoles( UserI user,String userId, List<String> roles) throws UserNotFoundException, UserInitException, InitializationException, InsufficientPrivilegesException;
}
