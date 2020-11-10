package org.nrg.xnat.services.users;

import java.util.List;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xft.security.UserI;

public interface UserService {

	public List<XdatUsergroup> findByProject(UserI user, String projectId);

	public List<XdatUsergroup> getUserGroupByProject(UserI sessionUser, String projectId);
}
