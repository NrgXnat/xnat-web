package org.nrg.xnat.services.resources.files;

import java.util.List;

import org.nrg.xapi.model.subjects.XnatProjectGroup;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.XftDataObjectService;

public interface ProjectGroupService extends XftDataObjectService<XnatProjectGroup> {

	public List<XnatProjectGroup> findProjectGroupByProjectId(UserI user, String projectId);
	
}
