package org.nrg.xnat.services.extensions;

import org.nrg.xft.security.UserI;

public interface VerifyExtensionsService {

	String findVerifyExtension(UserI user);
}
