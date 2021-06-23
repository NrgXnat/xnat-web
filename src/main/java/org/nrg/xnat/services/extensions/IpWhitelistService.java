package org.nrg.xnat.services.extensions;

import java.io.IOException;

import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xft.security.UserI;

public interface IpWhitelistService {

	String findAllIpWhiteList(UserI user) throws InsufficientPrivilegesException, InitializationException;
	
	void updateIpWhiteList(UserI user, String whitelist) throws InitializationException, IOException;
}
