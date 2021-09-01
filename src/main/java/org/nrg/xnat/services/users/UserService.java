package org.nrg.xnat.services.users;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xft.db.FavEntries;
import org.nrg.xft.security.UserI;

public interface UserService {

	 List<XdatUsergroup> findByProject(UserI user, String projectId) throws DataFormatException, NotFoundException;

	 List<XdatUsergroup> findUserGroupByProject(UserI sessionUser, String projectId) throws DataFormatException, NotFoundException;
	
	 Optional<XdatUsergroup> findUserGroupByGroupIdAndProject(UserI sessionUser, String groupId, String projectId) throws DataFormatException, NotFoundException;

	 List<FavEntries> FindAllUserFavorites(UserI user, String dataType ) throws DataFormatException, NotFoundException;
	 
	 Optional<FavEntries> findUserFavorite(UserI user, String projectId, String dataType ) throws NotFoundException, DataFormatException;
	 
	 List<FavEntries> updateUserFavorite(UserI user, String projectId, String dataType ) throws DataFormatException, NotFoundException;

	 void deleteUserFavorite(UserI user, String projectId, String dataType ) throws DataFormatException;
	 
	 void deleteByGroupIdAndProject(UserI sessionUser, String groupId, String projectId, String displayName) throws DataFormatException, NotFoundException;
	 
}
