package org.nrg.xnat.services.token;

import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xft.security.UserI;

public interface TokenService<T> {

	T findAll(UserI user, String operation, String tokenId, String secret, String requestedUserName) throws DataFormatException, NotFoundException, NotAuthenticatedException, InsufficientPrivilegesException;
}
