package org.nrg.xnat.services.resources;

import java.io.IOException;

public interface ProjectGroupResourceService {
	
	public void handleDelete();

	public void handlePost();

	public void handlePut();

	public String getProjectGroupResources(String projectId, String groupId) throws IOException, Exception;
}
