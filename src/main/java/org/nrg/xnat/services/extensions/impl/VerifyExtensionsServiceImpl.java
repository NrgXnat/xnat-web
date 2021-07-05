package org.nrg.xnat.services.extensions.impl;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.VerifyExtensionsService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VerifyExtensionsServiceImpl implements VerifyExtensionsService {

	@Override
	public String findVerifyExtension(UserI user) {
		 if (log.isDebugEnabled()) {
	            log.debug("Passing a representation of the verify extensions restlet.");
	        }
		return "This is to verify that dynamically configured restlets are functioning properly.";
	}

}
