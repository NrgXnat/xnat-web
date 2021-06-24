package org.nrg.xnat.services.extensions;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xft.security.UserI;

public interface SessionCountService {

	Integer findSessionCount(UserI user, String userName) throws DataFormatException, InsufficientPrivilegesException; 
}
