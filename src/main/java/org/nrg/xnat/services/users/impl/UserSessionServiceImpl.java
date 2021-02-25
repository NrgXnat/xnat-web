package org.nrg.xnat.services.users.impl;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserSessionService;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {
	
	@Override
	public String getJession(UserI user, HttpSession session, String csrf) {
		_includeXnatCsrfToken = BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBoolean(csrf), false);
		 return sessionIdRepresentation(session);
	}

	private String sessionIdRepresentation(HttpSession session) {
		return (session.getId() + (_includeXnatCsrfToken ? "; XNAT_CSRF=" + session.getAttribute("XNAT_CSRF") : ""));
	}

	private  boolean _includeXnatCsrfToken;
}
