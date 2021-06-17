package org.nrg.xnat.services.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.resource.DIRResourceDto;
import org.nrg.xnat.services.resources.impl.DIRResourceServiceImpl.InvalidFileCharacters;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface DIRResourceService {

	List<DIRResourceDto>  findAllDIRResources(UserI user, String projectId, String experimentId,String filepath, boolean recursive, boolean isXarReference) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters;

	StreamingResponseBody  findAllXARResources(UserI user, String projectId, String experimentId,String filepath, boolean recursive, boolean isXarReference, HttpServletRequest sRequest, HttpHeaders hRequest,String compression) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters;

	//String setContentDisposition(final String... parts);
}
