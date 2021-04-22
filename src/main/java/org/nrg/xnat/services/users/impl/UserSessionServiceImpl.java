package org.nrg.xnat.services.users.impl;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.BooleanUtils;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserSessionService;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {
	
	@Override
	public Optional<String> findJsession(UserI user, HttpSession session, String csrf) throws NotFoundException {
		_includeXnatCsrfToken = BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBoolean(csrf), false);
		String jsession = sessionIdRepresentation(session);
		if (Objects.isNull(jsession))
			throw new NotFoundException("Jsession was not found");

		return Optional.of(jsession);
	}

	private String sessionIdRepresentation(HttpSession session) {
		return (session.getId() + (_includeXnatCsrfToken ? "; XNAT_CSRF=" + session.getAttribute("XNAT_CSRF") : ""));
	}

	private  boolean _includeXnatCsrfToken;
}
