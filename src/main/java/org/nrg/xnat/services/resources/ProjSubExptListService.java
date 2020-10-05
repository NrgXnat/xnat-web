package org.nrg.xnat.services.resources;

import org.nrg.xft.security.UserI;

public interface ProjSubExptListService {
	
	public boolean allowPost();

	public void handlePost();

	public String getProjectExperiments(final UserI user, final String projectId, String experimentId) throws  Exception;
}
