package org.nrg.xnat.services.files;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface FileService {
	
	 List<XnatResourcecatalog> findByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findBySubjectId(UserI user, String subjectId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findByProjectIdAndResourceId(UserI user, String projectId, Integer resourceId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findBySubjectIdAndResourceId(UserI user, String subjectId, Integer resourceId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findByExperimentIdAndAssessorId(UserI user, String experimentId, String assessorId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findByExperimentId(UserI user, String experimentId) throws DataFormatException, NotFoundException;

	 List<XnatResourcecatalog> findByExperimentIdAndResourceId(UserI user, String experimentId, Integer resourceId) throws DataFormatException, NotFoundException;
	
	 void deleteResourceFile(UserI user,String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId, XnatEventUtil event) throws Exception;

	 Integer createResourceFile(UserI user, XnatResourceInfo xnatResourceInfo, String projectId, String subjectId, String experimentId,String assessorId, String scanId, String type, String resourceId, XnatEventUtil event) throws Exception;
}
