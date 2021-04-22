package org.nrg.xnat.services.users.impl;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import groovy.util.ResourceException;

@Service
public class UserAuthServiceImpl implements UserAuthService {

	@Override
	public Optional<String> getUserAuth(String csrf, String xnatCSRF) throws ResourceException, NotFoundException {
		_includeXnatCsrfToken = BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBoolean(csrf), false);
		UserI user = XDAT.getUserDetails();
		if (Objects.isNull(user)) 
			throw new ResourceException(HttpStatus.UNAUTHORIZED.toString());
		
		final String message = String.format(LOGGED_IN, user.getUsername()) + (_includeXnatCsrfToken ? "; XNAT_CSRF=" + xnatCSRF : "");
		if(Objects.isNull(message) || message.isEmpty())
	    		throw new  NotFoundException("User Session Not Found") ;
		return Optional.of(message);
	}

	private static final String LOGGED_IN = "User '%s' is logged in";
	private boolean _includeXnatCsrfToken;

}
