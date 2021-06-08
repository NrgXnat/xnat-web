package org.nrg.xnat.services.prearchive;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.action.ActionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.prearchive.PrearcSessionResourceDto;
import org.nrg.xnat.dto.prearchive.PrearcSessionScanDto;
import org.nrg.xnat.dto.prearchive.PrearcSessionScanResFileDto;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.helpers.prearchive.SessionException;

public interface PrearchiveService {

	List<PrearchiveDto> findAllPrearchives(UserI user, String projectId,  String tag) throws SQLException, SessionException, Exception;
	
	PrearchiveDto createPrarchiveRebuild(UserI user,  List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException, org.nrg.xapi.exceptions.DataFormatException;
	
	PrearchiveDto deletePrarchive(UserI user,  List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException;

	PrearchiveDto movePrarchive(UserI user,  List<String> src, String newProject) throws InitializationException, InsufficientPrivilegesException, NotFoundException, ResourceAlreadyExistsException, DataFormatException; 

	List<PrearcSessionResourceDto> findAllPrearcSessionResource(UserI user,String projectId, String timestamp, String sessionLabel) throws ActionException;

	List<PrearcSessionScanDto> findAllPrearcSessionScans(UserI user, String projectId, String timestamp, String sessionLabel) throws ActionException;

	List<PrearcSessionResourceDto> findAllPrearcSessionResourceByScanId(UserI user,String projectId, String timestamp, String sessionLabel, Integer scanId) throws ActionException, NotFoundException;
	
	List<PrearcSessionScanResFileDto> findAllPrearcSessionResourceByScanIdAndResourceId(UserI user,String projectId, String timestamp, String sessionLabel, Integer scanId, String resourceId, String filepath, boolean prettyPrint,HttpServletRequest request) throws ActionException, NotFoundException, DataFormatException;
}
