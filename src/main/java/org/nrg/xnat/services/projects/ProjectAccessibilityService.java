package org.nrg.xnat.services.projects;

import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface ProjectAccessibilityService {
	
	public Optional<String> findByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException;
	
	public Optional<String> findByProjectIdAndAccessLevel(UserI user, String projectId, String accessLevel) throws NotFoundException, DataFormatException;
	
	public String update(UserI user, String access, String projectId) throws NotFoundException, InsufficientPrivilegesException, Exception;
}
