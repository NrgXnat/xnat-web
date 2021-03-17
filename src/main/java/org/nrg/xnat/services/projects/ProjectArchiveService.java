package org.nrg.xnat.services.projects;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xft.security.UserI;

public interface ProjectArchiveService {

	public ArcProject findArcProjectByProjectId(UserI user, String projectId) throws NotFoundException;
}
