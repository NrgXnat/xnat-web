package org.nrg.xnat.services.par;

import java.util.List;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;

public interface PARService {

	public List<ProjectAccessRequest> findAllProjectAccessRequests(UserI user) throws InitializationException;
	
	public ProjectAccessRequest findParResourceByParId(UserI user, Integer parId) throws DataFormatException;
	
	public List<ProjectAccessRequest> findProjectParsByProjectId(UserI user, String projectId) throws DataFormatException;
	
	public ProjectAccessRequest update(UserI user, ProjectAccessRequest projectAccessRequest, Integer parId) throws Exception;
	
}
