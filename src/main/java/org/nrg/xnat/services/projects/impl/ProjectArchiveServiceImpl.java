package org.nrg.xnat.services.projects.impl;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.projects.ProjectArchiveService;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.springframework.stereotype.Service;

@Service
public class ProjectArchiveServiceImpl implements ProjectArchiveService {

	@Override
	public ArcProject findArcProjectByProjectId(UserI user, String projectId) throws NotFoundException {
		XnatProjectdata proj = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if (proj != null) {
			return  ArcSpecManager.GetFreshInstance().getProjectArc(proj.getId());
		} else {
			throw new NotFoundException("Unable to find the specified scan.");
		}
	}
}
