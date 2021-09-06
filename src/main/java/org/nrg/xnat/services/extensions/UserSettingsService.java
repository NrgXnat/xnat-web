package org.nrg.xnat.services.extensions;

import java.util.List;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.model.users.User;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;

public interface UserSettingsService {

	List<String> findAllUser(UserI user) throws InitializationException, InsufficientPrivilegesException;
	
	User findUserByUserId(UserI user, String userId) throws InitializationException, InsufficientPrivilegesException, UserNotFoundException, UserInitException;

	void updateUserAction(String action, String userId) throws InitializationException, DataFormatException;
	
	void deleteUserAction(String action, String userIds) throws InitializationException, DataFormatException;
}
