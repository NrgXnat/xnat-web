package org.nrg.xnat.services.extensions;

import java.util.List;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.extensions.util.StudyRoutingUtil;

public interface StudyRoutingService {
	
	List<StudyRoutingUtil> findAll(UserI user) throws InitializationException, NotFoundException;
	
	StudyRoutingUtil findByStudyInstanceUid(UserI user, String studyInstanceUid) throws NotFoundException, InitializationException;
	
	void updateStudyRouting(UserI user, String studyInstanceUid, String projectId) throws InitializationException, DataFormatException;
	
	void deleteStudyRouting(UserI user, String studyInstanceUid) throws InsufficientPrivilegesException, InitializationException;
	
}
