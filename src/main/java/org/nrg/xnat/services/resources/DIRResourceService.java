package org.nrg.xnat.services.resources;

import javax.servlet.http.HttpServletRequest;

import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.impl.DIRResourceServiceImpl.InvalidFileCharacters;
import org.springframework.http.HttpHeaders;

public interface DIRResourceService {

	void  findAllDIRResources(UserI user, String projectId, String experimentId,String filepath, boolean recursive, boolean isXarReference, HttpHeaders request, HttpServletRequest sRequest, String compression ) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters;
}
