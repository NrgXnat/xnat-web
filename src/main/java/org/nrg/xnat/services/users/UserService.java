package org.nrg.xnat.services.users;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xft.security.UserI;

public interface UserService {

	public Optional<List<XdatUsergroup>> findByProject(UserI user, String projectId) throws DataFormatException, NotFoundException;

	public Optional<List<XdatUsergroup>> findUserGroupByProject(UserI sessionUser, String projectId) throws DataFormatException, NotFoundException;
	
	public Optional<XdatUsergroup> findUserGroupByGroupIdAndProject(UserI sessionUser, String groupId, String projectId) throws DataFormatException, NotFoundException;
}
