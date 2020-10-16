package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xapi.model.subjects.XnatProject;
import org.nrg.xft.security.UserI;

public interface ProjectListService {
	
	public XnatProject create(UserI user, XnatProject item);

	public List<XnatProject> getAll(UserI user);

	public XnatProject get(UserI user, int itemId);

	public XnatProject findById(UserI user, String itemId);
}
