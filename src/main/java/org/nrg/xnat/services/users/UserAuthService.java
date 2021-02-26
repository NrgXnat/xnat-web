package org.nrg.xnat.services.users;

import groovy.util.ResourceException;

public interface UserAuthService {
	
	public String getUserAuth(String csrf, String xnatCSRF) throws ResourceException;
}
