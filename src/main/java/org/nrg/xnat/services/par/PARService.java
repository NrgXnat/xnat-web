package org.nrg.xnat.services.par;

import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;

public interface PARService {

	public void getParList(UserI user) throws InitializationException;
	
	public void getParResourceByParId(UserI user, String parId);
	
	public void getProjectParListByProjectId(UserI user, String projectId);
}
