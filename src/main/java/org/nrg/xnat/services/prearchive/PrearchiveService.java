package org.nrg.xnat.services.prearchive;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xft.security.UserI;
import org.nrg.xapi.model.PrearcSessionResource;
import org.nrg.xapi.model.PrearcSessionScan;
import org.nrg.xapi.model.PrearcSessionScanResFile;
import org.nrg.xapi.model.Prearchive;
import org.nrg.xnat.helpers.prearchive.SessionException;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;

public interface PrearchiveService {

	List<Prearchive> findAllPrearchives(UserI user, String projectId, String tag) throws SQLException, SessionException, Exception;
	
	Prearchive createPrarchiveRebuild(UserI user, List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException, org.nrg.xapi.exceptions.DataFormatException;
	
	Prearchive deletePrarchive(UserI user, List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException;

	Prearchive movePrarchive(UserI user, List<String> src, String newProject) throws InitializationException, InsufficientPrivilegesException, NotFoundException, ResourceAlreadyExistsException, DataFormatException;

	List<PrearcSessionResource> findAllPrearcSessionResource(UserI user, String projectId, String timestamp, String sessionLabel) throws ActionException;

	List<PrearcSessionScan> findAllPrearcSessionScans(UserI user, String projectId, String timestamp, String sessionLabel) throws ActionException;

	List<PrearcSessionResource> findAllPrearcSessionResourceByScanId(UserI user, String projectId, String timestamp, String sessionLabel, Integer scanId) throws ActionException, NotFoundException;
	
	List<PrearcSessionScanResFile> findAllPrearcSessionResourceByScanIdAndResourceId(UserI user, String projectId, String timestamp, String sessionLabel, Integer scanId, String resourceId, String filepath, boolean prettyPrint, HttpServletRequest request) throws ActionException, NotFoundException, DataFormatException;

	List<String> importFiles(UserI user, HttpServletRequest request,XnatResourceInfo xnatResourceInfo) throws DataFormatException, ServerException, ClientException, NotFoundException;
}
