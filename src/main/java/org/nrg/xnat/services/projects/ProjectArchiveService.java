package org.nrg.xnat.services.projects;

import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xft.security.UserI;

public interface ProjectArchiveService {

	public Optional<ArcProject> findByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException;
}
