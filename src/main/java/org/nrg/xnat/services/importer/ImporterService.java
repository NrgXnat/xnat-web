package org.nrg.xnat.services.importer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;

public interface ImporterService {
	
	List<String> importFiles(UserI user, HttpServletRequest request,XnatResourceInfo xnatResourceInfo) throws DataFormatException, ServerException, ClientException, NotFoundException;
}
