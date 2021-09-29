package org.nrg.xnat.services.files;

import java.util.List;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xapi.model.ResourceFile;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface FileService {
	
	 List<ResourceFile> findByProjectId(UserI user, String projectId, String[] contents, String[] formats) throws DataFormatException, NotFoundException;

	 List<ResourceFile> findBySubjectId(UserI user, String subjectId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndResourceId(UserI user, String projectId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException;

	 List<ResourceFile> findBySubjectIdAndResourceId(UserI user, String subjectId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByExperimentIdAndAssessorId(UserI user, String experimentId, String assessorId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByExperimentId(UserI user, String experimentId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByExperimentIdAndResourceId(UserI user, String experimentId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;
	
	 void deleteResourceFile(UserI user,String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId, boolean removeFiles, XnatEventUtil event) throws Exception;

	 Integer createResourceFile(UserI user, XnatResourceInfo xnatResourceInfo, String projectId, String subjectId, String experimentId,String assessorId, String scanId, String type, String resourceId, XnatEventUtil event) throws Exception;

	 List<ResourceFile> findByExperimentIdAndAssessorIdAndResourceId(UserI user, String experimentId, String assessorId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;
}
