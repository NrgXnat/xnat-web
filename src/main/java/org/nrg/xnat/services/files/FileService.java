package org.nrg.xnat.services.files;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

public interface FileService {
	
	List<XnatResourcecatalog> findByProject(UserI user, String projectId);

	List<XnatResourcecatalog> findBySubject(UserI user, String subjectId);

	List<XnatResourcecatalog> findByProjectAndSubject(UserI user, String projectId, String subjectId);

	List<XnatResourcecatalog> findByProjectAndResource(UserI user, String projectId, Integer resourceId);

	List<XnatResourcecatalog> findBySubjectAndResource(UserI user, String subjectId, Integer resourceId);

	List<XnatResourcecatalog> findByExperimentAndAssessors(UserI user, String experimentId, String assessorId);

	List<XnatResourcecatalog> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId);

	List<XnatResourcecatalog> findByExperiment(UserI user, String experimentId);

	List<XnatResourcecatalog> findByExperimentAndResource(UserI user, String experimentId, Integer resourceId);
	
	public void createResourceFile(UserI user,  HttpServletRequest request, String projectId, String resourceId, String requestRename, String requestDesc, String requestFormat, String requestContent, String[] requestTags );
	
	public void deleteResourceFile(UserI user,String projectId, String resourceId);
}
