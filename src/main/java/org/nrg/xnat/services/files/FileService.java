package org.nrg.xnat.services.files;

import java.util.List;

import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;

public interface FileService {
	
	List<XnatResourcecatalog> findByProject(UserI user, String projectId);

	List<XnatResourcecatalog> findBySubject(UserI user, String subjectId);

	List<XnatResourcecatalog> findByProjectAndSubject(UserI user, String projectId, String subjectId);

	List<XnatResourcecatalog> findByProjectAndResource(UserI user, String projectId, Integer resourceId);

	List<XnatResourcecatalog> findBySubjectAndResource(UserI user, String subjectId, Integer resourceId);

	List<XnatResourcecatalog> findByExperimentAndAssessors(UserI user, String experimentId, String assessorId);

	List<XnatResourcecatalog> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId);
}
