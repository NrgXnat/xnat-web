package org.nrg.xnat.services.extensions.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.SessionCountService;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SessionCountServiceImpl implements SessionCountService {

	@Override
	public Integer findSessionCount(UserI user, String username) throws DataFormatException, InsufficientPrivilegesException {
		UserI validUser = validateUser(user, username);
		if (log.isDebugEnabled()) {
			log.debug("Entering the session count represent() method");
		}
		return getSessionCount(validUser);
	}

	private int getSessionCount(UserI user) {
		SessionRegistry sessionRegistry = XDAT.getContextService().getBean("sessionRegistry", SessionRegistryImpl.class);
		int sessionCount = 0;
		if (sessionRegistry != null) {
			List<SessionInformation> l = sessionRegistry.getAllSessions(user, false);
			if (l != null) {
				sessionCount = l.size();
			}
		}
		return sessionCount;
	}

	private UserI validateUser(UserI user, String username) throws DataFormatException, InsufficientPrivilegesException {
		if (!StringUtils.isBlank(username)) {
            // But if it's just you, no harm no foul.
            if (username.equals(user.getLogin())) {
            	return user;
            } else if (!Roles.isSiteAdmin(user)) {
                // If it's NOT you and you're not an admin, you are banished.
            	throw new InsufficientPrivilegesException("Only site admins can request the session count for another user.");
            } else {
                // If you are an admin and this isn't you, then let's get that account.
            	return getXdatValidUser(username);
            }
		}else {
            return user;
        }
	}

	private UserI getXdatValidUser(String username) throws DataFormatException {
		UserI xdatUser=null;
		try {
			xdatUser = Users.getUser(username);
		} catch (UserNotFoundException | UserInitException e) {
			log.error("",e);
		}
		
		if (xdatUser == null) {
			throw new DataFormatException("The user identified by " + username + " can not be found in the system.");
		} 
		return xdatUser;
	}

}
