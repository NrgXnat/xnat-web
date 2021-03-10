package org.nrg.xnat.services.files;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

public interface FileService {
	
	List<XnatResourcecatalog> findByProject(UserI user, String projectId) throws DataFormatException;

	List<XnatResourcecatalog> findBySubject(UserI user, String subjectId) throws DataFormatException;

	List<XnatResourcecatalog> findByProjectAndSubject(UserI user, String projectId, String subjectId) throws DataFormatException;

	List<XnatResourcecatalog> findByProjectAndResource(UserI user, String projectId, Integer resourceId) throws DataFormatException;

	List<XnatResourcecatalog> findBySubjectAndResource(UserI user, String subjectId, Integer resourceId) throws DataFormatException;

	List<XnatResourcecatalog> findByExperimentAndAssessors(UserI user, String experimentId, String assessorId) throws DataFormatException;

	List<XnatResourcecatalog> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId) throws DataFormatException;

	List<XnatResourcecatalog> findByExperiment(UserI user, String experimentId) throws DataFormatException;

	List<XnatResourcecatalog> findByExperimentAndResource(UserI user, String experimentId, Integer resourceId) throws DataFormatException;
	
	public void createResourceFile(UserI user,  HttpServletRequest request, String projectId, String resourceId, String requestRename, String requestDesc, String requestFormat, String requestContent, String[] requestTags );
	
	public void deleteResourceFile(UserI user,String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId) throws Exception;
}
