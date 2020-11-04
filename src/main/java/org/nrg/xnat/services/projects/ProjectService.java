package org.nrg.xnat.services.projects;

import java.util.List;

import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;

public interface ProjectService {

	public List<XnatProjectdata> getAll(UserI user);

	public XnatProjectdata findById(UserI user, String projectId);
	
	public XnatProjectdata create(UserI user, XnatProjectdata xnatProjectdata);

	public XnatProjectdata update(UserI user, XnatProjectdata xnatProjectdata, String projectId);

	public void deleteById(UserI user, String projectId);
}
