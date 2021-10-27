package org.nrg.xnat.services.users;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XdatUsergroupI;
import org.nrg.xft.db.FavEntries;
import org.nrg.xft.security.UserI;

// TODO: Major issue here: there are TWO XdatUsergroupI interfaces, org.nrg.xdat.model.XdatUsergroupI and org.nrg.xdat.om.XdatUsergroupI. XdatUsergroup actually implements the latter.
public interface UserService {

	 List<XdatUsergroupI> findByProject(UserI user, String projectId) throws DataFormatException, NotFoundException;

	 List<XdatUsergroupI> findUserGroupByProject(UserI sessionUser, String projectId) throws DataFormatException, NotFoundException;
	
	 Optional<XdatUsergroupI> findUserGroupByGroupIdAndProject(UserI sessionUser, String groupId, String projectId) throws DataFormatException, NotFoundException;

	 List<FavEntries> FindAllUserFavorites(UserI user, String dataType ) throws DataFormatException, NotFoundException;
	 
	 Optional<FavEntries> findUserFavorite(UserI user, String projectId, String dataType ) throws NotFoundException, DataFormatException;
	 
	 List<FavEntries> updateUserFavorite(UserI user, String projectId, String dataType ) throws DataFormatException, NotFoundException;

	 void deleteUserFavorite(UserI user, String projectId, String dataType ) throws DataFormatException;
	 
	 void deleteByGroupIdAndProject(UserI sessionUser, String groupId, String projectId, String displayName) throws DataFormatException, NotFoundException;
	 
	 void updateByGroupIdAndProject(UserI user, XdatUsergroupI group, String groupId, String projectId, Map<String, Object> groupProperties) throws InitializationException, DataFormatException;

	 //Session Count Service
	 Integer findSessionCount(UserI user, String userName) throws DataFormatException, InsufficientPrivilegesException;

	 //User Cache service
	 void findUserCacheResourceByXname(String xName);

	 void findUserCacheResourceFilesByXname(String xName);

	 void findUserCacheResourceFilesByXnameAndFileName(String xName, String fileName);
	 
	 //User Favorite Service 
	 void findUserFavoritesByDataType(UserI user, String dataType);
		
	 void findUserFavoritesByDataTypeAndProjectId(UserI user, String dataType, String projectId);
	
	 void delete(UserI user, String dataType, String projectId) throws NotFoundException;
	
	 void update(UserI user, String dataType, String projectId) throws NotFoundException;
	
	 

}
