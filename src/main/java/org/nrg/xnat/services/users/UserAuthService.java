package org.nrg.xnat.services.users;

import java.util.Optional;

import org.nrg.xapi.exceptions.NotFoundException;

import groovy.util.ResourceException;

public interface UserAuthService {
	
	public Optional<String> getUserAuth(String csrf, String xnatCSRF) throws ResourceException, NotFoundException ;
}
