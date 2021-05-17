package org.nrg.xnat.services.files;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.file.ResourceFileDto;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface FileService {
	
	 List<ResourceFileDto> findByProjectId(UserI user, String projectId,String[] contents,String[] formats) throws DataFormatException, NotFoundException;

	 List<ResourceFileDto> findBySubjectId(UserI user, String subjectId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFileDto> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFileDto> findByProjectIdAndResourceId(UserI user, String projectId, Integer resourceId, String[] contents,String[] formats) throws DataFormatException, NotFoundException;

	 List<ResourceFileDto> findBySubjectIdAndResourceId(UserI user, String subjectId, Integer resourceId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFileDto> findByExperimentIdAndAssessorId(UserI user, String experimentId, String assessorId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFileDto> findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFileDto> findByExperimentId(UserI user, String experimentId,String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFileDto> findByExperimentIdAndResourceId(UserI user, String experimentId, Integer resourceId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;
	
	 void deleteResourceFile(UserI user,String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId, XnatEventUtil event) throws Exception;

	 Integer createResourceFile(UserI user, XnatResourceInfo xnatResourceInfo, String projectId, String subjectId, String experimentId,String assessorId, String scanId, String type, String resourceId, XnatEventUtil event) throws Exception;

	List<ResourceFileDto> findByExperimentIdAndAssessorIdAndResourceId(UserI user, String experimentId, String assessorId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;
}
