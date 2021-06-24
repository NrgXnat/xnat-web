package org.nrg.xnat.services.extensions;

import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.impl.SendEmailVerificationServiceImpl.EmailNotFoundException;
import org.nrg.xnat.services.extensions.impl.SendEmailVerificationServiceImpl.ExceededRequestsException;

public interface SendEmailVerificationService {

	void sendEmail(UserI user, String email) throws ExceededRequestsException, EmailNotFoundException, InitializationException;
}
