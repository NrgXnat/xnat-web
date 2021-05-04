package org.nrg.xnat.services.users;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface UserFavoriteService {

	 void findUserFavoritesByDataType(UserI user, String dataType);
	
	 void findUserFavoritesByDataTypeAndProjectId(UserI user, String dataType, String projectId);
	
	 void delete(UserI user, String dataType, String projectId) throws NotFoundException;
	
	 void update(UserI user, String dataType, String projectId) throws NotFoundException;
}
