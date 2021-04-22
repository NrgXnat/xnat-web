package org.nrg.xnat.services.users;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface UserSessionService {

	public Optional<String> findJsession(UserI user, HttpSession session, String csrf) throws NotFoundException;
}
