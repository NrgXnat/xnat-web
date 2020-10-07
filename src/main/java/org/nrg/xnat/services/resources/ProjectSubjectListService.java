package org.nrg.xnat.services.resources;

import java.io.IOException;

public interface ProjectSubjectListService {
	
	public String getProjectSubjectById(String projectId) throws IOException;

	public String getProjectSubjectById(String projectId, String subjectId) throws IOException;

	public String getProjectSubjectResource(String projectId, String subjectId, String experimentId) throws Exception;

	public String getProjectSubjectExperimentResource(String projectId, String subjectId, String experimentId) throws IOException;

	public String getProjectSubjectExperimentResources(String projectId, String subjectId, String experimentId,String resourceId) throws IOException;
}