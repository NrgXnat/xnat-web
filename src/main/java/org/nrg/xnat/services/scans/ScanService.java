package org.nrg.xnat.services.scans;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface ScanService {
	
	 List<XnatImagescandata> findAllScanTypesByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;

	 List<XnatImagescandata> findAllScanTypes(UserI user) throws NotFoundException;
	
	 List<XnatImagescandata> findAllByAssessedId(UserI user, String assessedId) throws DataFormatException, NotFoundException;

	 Optional<XnatImagescandata> findByAssessedIdAndScanId(UserI user, String assessedId, Integer scanId) throws DataFormatException, NotFoundException;

	 List<XnatImagescandata> findAllByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId) throws DataFormatException, NotFoundException;

	 Optional<XnatImagescandata> findByProjectIdAndSubjectIdAndExperimentIdAndScanId(UserI user, String projectId, String subjectId, String experimentId, Integer scanId) throws DataFormatException, NotFoundException;

	 void deleteById(UserI user, String assessedId, Integer scanId, String filepath, boolean removeFiles,XnatEventUtil event) throws NotFoundException, DataFormatException, InitializationException;
	
	 List<Map<String, String>> findAllScanners(UserI user, String scanTable, String projectId) throws InsufficientPrivilegesException;

	 //Scan Quality Label Service
	 String findAllScanQualityLable(UserI user, String projectId) throws InitializationException;  
}
