package org.nrg.xnat.services.users;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface UserFavoriteService {

	public void findUserFavoritesByDataType(UserI user, String dataType);
	
	public void findUserFavoritesByDataTypeAndProjectId(UserI user, String dataType, String projectId);
	
	public void delete(UserI user, String dataType, String projectId) throws NotFoundException;
	
	public void update(UserI user, String dataType, String projectId) throws NotFoundException;
}
