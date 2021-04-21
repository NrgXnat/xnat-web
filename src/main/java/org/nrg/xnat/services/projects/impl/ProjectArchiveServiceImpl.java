package org.nrg.xnat.services.projects.impl;

import java.util.Objects;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
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
	public Optional<ArcProject> findByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException {
		if(Objects.isNull(projectId))
    		throw new DataFormatException("The requested projectId wasn't found ");
		XnatProjectdata proj = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if(Objects.isNull(proj))
    		throw new  NotFoundException(ArcProject.SCHEMA_ELEMENT_NAME, projectId) ;
		ArcProject arcProj=   ArcSpecManager.GetFreshInstance().getProjectArc(proj.getId());
		if(Objects.isNull(arcProj))
    		throw new  NotFoundException(ArcProject.SCHEMA_ELEMENT_NAME, projectId) ;
		return Optional.of(arcProj);
	}
}
