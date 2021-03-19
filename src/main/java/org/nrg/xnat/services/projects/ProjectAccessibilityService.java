package org.nrg.xnat.services.projects;

import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface ProjectAccessibilityService {
	
	public String findByProjectId(UserI user, String projectId) throws NotFoundException, Exception;
	
	public String findByProjectIdAndAccessLevel(UserI user, String projectId, String accessLevel) throws NotFoundException, Exception;
	
	public String update(UserI user, String access, String projectId) throws NotFoundException, InsufficientPrivilegesException, Exception;
}
