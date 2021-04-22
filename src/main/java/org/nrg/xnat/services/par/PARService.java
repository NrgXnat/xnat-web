package org.nrg.xnat.services.par;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;

public interface PARService {

	public Optional<List<ProjectAccessRequest>> findAll(UserI user) throws NotFoundException;
	
	public Optional<ProjectAccessRequest> findByParId(UserI user, Integer parId) throws DataFormatException, NotFoundException;
	
	public Optional<List<ProjectAccessRequest>> findByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException ;
	
	public ProjectAccessRequest update(UserI user, ProjectAccessRequest projectAccessRequest, Integer parId, String accept, String decline) throws Exception;
	
}
