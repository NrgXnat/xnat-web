package org.nrg.xnat.services.prearchive;

import java.sql.SQLException;
import java.util.List;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.helpers.prearchive.SessionException;

public interface PrearchiveService {

	List<PrearchiveDto> findAllPrearchives(UserI user, String projectId,  String tag) throws SQLException, SessionException, Exception;
	
	PrearchiveDto createPrarchiveRebuild(UserI user,  List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException, org.nrg.xapi.exceptions.DataFormatException;
	
	PrearchiveDto deletePrarchive(UserI user,  List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException;
}
