package org.nrg.xnat.services.users;

import javax.servlet.http.HttpSession;

import org.nrg.xft.security.UserI;

public interface UserSessionService {

	public String getJession(UserI user, HttpSession session, String csrf);
}
