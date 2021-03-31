package org.nrg.xnat.services.par;

import java.util.List;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;

public interface PARService {

	public void getParList(UserI user) throws InitializationException;
	
	public ProjectAccessRequest getParResourceByParId(UserI user, Integer parId) throws DataFormatException;
	
	public List<ProjectAccessRequest> getProjectParsByProjectId(UserI user, String projectId) throws DataFormatException;
	
}
