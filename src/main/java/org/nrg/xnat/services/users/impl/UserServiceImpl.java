package org.nrg.xnat.services.users.impl;

import static org.nrg.xdat.om.base.auto.AutoXdatUsergroup.SCHEMA_ELEMENT_NAME;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XdatUsergroupI;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.ElementSecurity;
import org.nrg.xdat.security.PermissionCriteria;
import org.nrg.xdat.security.PermissionCriteriaI;
import org.nrg.xdat.security.UserGroupI;
import org.nrg.xdat.security.UserGroupServiceI;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.UserHelper;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.FavEntries;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserService;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

	@Autowired
	public UserServiceImpl(final NamedParameterJdbcTemplate template, final UserGroupServiceI serivce) {
		_template = template;
		_service =  serivce;
	}
	
	@Override
	public List<XdatUsergroupI> findByProject(UserI user, String projectId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		List<XdatUsergroupI> users = _template.query(USER_QUERY + BY_ID_WHERE_PRO, new MapSqlParameterSource("projectId", projectId), new UserRowMapper(user));
		if(Objects.isNull(users) || users.isEmpty()) {
    		throw new  NotFoundException(XdatUsergroup.SCHEMA_ELEMENT_NAME, projectId) ;
		}
    	return users;
	}
	
	@Override
	public List<XdatUsergroupI> findUserGroupByProject(UserI user, String projectId) throws DataFormatException, NotFoundException {
		ValidateProjectId(projectId);
		
		List<XdatUsergroupI> userGroups = _template.query(USER_GROUP_QUERY + BY_ID_WHERE_USER_GROUP_PROJECT + USER_GROUP_BY, new MapSqlParameterSource("projectId", projectId), new UserGroupRowMapper(user));
		if(Objects.isNull(userGroups) || userGroups.isEmpty()) {
    		throw new  NotFoundException(XdatUsergroup.SCHEMA_ELEMENT_NAME, projectId) ;
		}
    	return userGroups;
	}
	
	@Override
	public Optional<XdatUsergroupI> findUserGroupByGroupIdAndProject(UserI user, String groupId, String projectId) throws DataFormatException, NotFoundException {
		validateGroupIdAndProjectId(groupId, projectId);
		
		XdatUsergroupI userGroup = _template.queryForObject(USER_GROUP_QUERY + BY_ID_WHERE_USER_GROUP_PROJECT + BY_GROUP_ID_WHERE + USER_GROUP_BY, new MapSqlParameterSource("projectId", projectId).addValue("groupId", groupId), new UserGroupRowMapper(user));
		if(Objects.isNull(userGroup)) {
    		throw new  NotFoundException(XdatUsergroup.SCHEMA_ELEMENT_NAME, groupId) ;
		}
    	return Optional.of(userGroup);
	}
	
	@Override
	public List<FavEntries> FindAllUserFavorites(UserI user, String dataType) throws DataFormatException, NotFoundException {
		validateDataType(dataType);
		List<FavEntries> fanEntries = _template.query(BY_DATATYPE_USERID_WHERE_USER_FAVORITE_QUERY, new MapSqlParameterSource("dataType", dataType).addValue("userId", user.getID()), new UserFavoriteRowMapper());
		if(Objects.isNull(fanEntries) || fanEntries.isEmpty()) {
    		throw new  NotFoundException("The requested FavEntries for data Type " + dataType + "wasn't found" ) ;
		}
		return fanEntries;
	}

	
	@Override
	public  Optional<FavEntries> findUserFavorite(UserI user, String projectId, String dataType) throws NotFoundException, DataFormatException {
		ValidateProjectId(projectId);
		validateDataType(dataType);
		FavEntries favEntries =  _template.queryForObject(BY_ID_DATATYPE_USERID_WHERE_USER_FAVORITE_QUERY, new MapSqlParameterSource("projectId", projectId).addValue("dataType", dataType).addValue("userId", user.getID()), new UserFavoriteRowMapper());
		if(Objects.isNull(favEntries)) {
    		throw new  NotFoundException("The requested FavEntries for project ID " + projectId + "wasn't found" ) ;
		}
    	return Optional.of(favEntries);
	}

	@Override
	public void deleteUserFavorite(UserI user, String projectId, String dataType) throws DataFormatException {
		ValidateProjectId(projectId);
		validateDataType(dataType);
		try {
			FavEntries favEntry=FavEntries.GetFavoriteEntries(dataType, projectId, user);
			if(Objects.isNull(favEntry)) {
	    		throw new  NotFoundException("The requested FavEntries for project ID " + projectId + "wasn't found" ) ;
			}
			favEntry.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<FavEntries> updateUserFavorite(UserI user, String projectId, String dataType) throws DataFormatException, NotFoundException {
		ValidateProjectId(projectId);
		validateDataType(dataType);
		try {
			FavEntries favEntry=new FavEntries();
			favEntry.setId(projectId);
			favEntry.setDataType(dataType);
			favEntry.setUser(user);
			favEntry.save();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FindAllUserFavorites(user, dataType);
	}
	
	@Override
	public void deleteByGroupIdAndProject(UserI user, String groupId, String projectId, String displayName) throws DataFormatException, NotFoundException {
		validateGroupIdAndProjectId(groupId, projectId);
		
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		
		UserGroupI group = findGroupByNameAndDisplayName(groupId, displayName, project);
		if (PROTECTED_DISPLAY_NAMES.contains(group.getDisplayname())) {
			throw new NotFoundException("Display name wasn't found");
		}
		
		try {
			if (!UserHelper.getUserHelperService(user).canDelete(project)) {
				throw new InsufficientPrivilegesException("Specified user account has insufficient delete privileges for project in this group.");
			}
			final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(null, user, XnatProjectdata.SCHEMA_ELEMENT_NAME, project.getId(), project.getId(), EventUtils.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, EventUtils.TYPE.WEB_SERVICE, "Remove Group"));
			final EventMetaI ci = workflow.buildEvent();
			_service.deleteGroup(group, user, ci);
			WorkflowUtils.complete(workflow, ci);
		} catch (Exception e) {
			log.error("project group wasn't not deleted" +  e.getLocalizedMessage());
		}
	}
	
	@Override
	public void updateByGroupIdAndProject(UserI user, XdatUsergroupI group, String groupId, String projectId, Map<String, Object> groupProperties) throws InitializationException, DataFormatException {
		
		validateGroupIdAndProjectId(groupId, projectId);
		
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		
		UserGroupI userGroup = findGroupByNameAndDisplayName(groupId, group.getDisplayname(), project);
		
		try {
            final UserGroupI working = _service.createGroup(groupProperties);

            //tag must be for this project
            if (!StringUtils.equals(project.getId(), working.getTag())) {
                working.setTag(project.getId());
            }

           validateDisplayName(working);

            //set ID to the standard value
            if (StringUtils.isEmpty(working.getId())) {
                working.setId(project.getId() + "_" + StringUtils.removeAll(working.getDisplayname(), "\\s?"));
            }

            final List<PermissionCriteriaI> newPermissions = getNewPermissions(working, groupProperties);

            final PersistentWorkflowI wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, SCHEMA_ELEMENT_NAME, working.getTag(), working.getId(), EventUtils.newEventInstance(EventUtils.CATEGORY.PROJECT_ACCESS, EventUtils.TYPE.WEB_SERVICE, (userGroup == null) ? "Added user group" : "Modified user group."));
            assert wrk != null;

            //need to pre-create the group if it doesn't already exist
            if (userGroup == null) {
                _service.save(working, user, wrk.buildEvent());
            }

            Permissions.setPermissionsForGroup(working, newPermissions, wrk.buildEvent(), user);
            Groups.save(working, user, wrk.buildEvent());
            WorkflowUtils.complete(wrk, wrk.buildEvent());
            Groups.reloadGroupsForUser(user);

        } catch (Exception e) {
            log.error("", e);
            throw new InitializationException(e.getLocalizedMessage());
        }
	}
	
	
	@Override
	public Integer findSessionCount(UserI user, String username) throws DataFormatException, InsufficientPrivilegesException {
		UserI validUser = validateUser(user, username);
		if (log.isDebugEnabled()) {
			log.debug("Entering the session count represent() method");
		}
		return getSessionCount(validUser);
	}

	@Override
	public void findUserCacheResourceByXname(String xName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void findUserCacheResourceFilesByXname(String xName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void findUserCacheResourceFilesByXnameAndFileName(String xName, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void findUserFavoritesByDataType(UserI user, String dataType) {
		XFTTable table = null;
		if (dataType != null) {
			try {
				table = FavEntries.GetFavoriteEntries(dataType, user);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (DBPoolException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void findUserFavoritesByDataTypeAndProjectId(UserI user, String dataType, String projectId) {
		findUserFavoritesByDataType(user, dataType);
	}

	@Override
	public void delete(UserI user, String dataType, String projectId) throws NotFoundException {
		if (projectId == null || dataType == null || user == null) {
			throw new NotFoundException("");
		} else {
			try {
				FavEntries favEntry = FavEntries.GetFavoriteEntries(dataType, projectId, user);
				favEntry.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(UserI user, String dataType, String projectId) throws NotFoundException {
		if (projectId == null || dataType == null) {
			throw new NotFoundException("");
		} else {
			try {
				FavEntries favEntry = new FavEntries();
				favEntry.setId(projectId);
				favEntry.setDataType(dataType);
				favEntry.setUser(user);
				favEntry.save();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private int getSessionCount(UserI user) {
		SessionRegistry sessionRegistry = XDAT.getContextService().getBean("sessionRegistry", SessionRegistryImpl.class);
		int sessionCount = 0;
		if (sessionRegistry != null) {
			List<SessionInformation> l = sessionRegistry.getAllSessions(user, false);
			if (l != null) {
				sessionCount = l.size();
			}
		}
		return sessionCount;
	}

	private UserI validateUser(UserI user, String username) throws DataFormatException, InsufficientPrivilegesException {
		if (!StringUtils.isBlank(username)) {
            // But if it's just you, no harm no foul.
            if (username.equals(user.getLogin())) {
            	return user;
            } else if (!Roles.isSiteAdmin(user)) {
                // If it's NOT you and you're not an admin, you are banished.
            	throw new InsufficientPrivilegesException("Only site admins can request the session count for another user.");
            } else {
                // If you are an admin and this isn't you, then let's get that account.
            	return getXdatValidUser(username);
            }
		}else {
            return user;
        }
	}

	private UserI getXdatValidUser(String username) throws DataFormatException {
		UserI xdatUser=null;
		try {
			xdatUser = Users.getUser(username);
		} catch (UserNotFoundException | UserInitException e) {
			log.error("",e);
		}
		
		if (xdatUser == null) {
			throw new DataFormatException("The user identified by " + username + " can not be found in the system.");
		} 
		return xdatUser;
	}
	
	
	private void validateDisplayName(UserGroupI working) throws DataFormatException {
		 //display name is required
        if (StringUtils.isEmpty(working.getDisplayname())) {
        	throw new DataFormatException("The requested displayName" + working.getDisplayname() + " wasn't found ");
        }
        //display name cannot contain underscore
        if (working.getDisplayname().contains("_")) {
        	throw new DataFormatException("The requested displayName" + working.getDisplayname() + "  cannot contain underscores ");
        }
		
	}

	private void validateGroupIdAndProjectId(String groupId, String projectId) throws DataFormatException {
		if (StringUtils.isBlank(projectId)) {
			throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		if (StringUtils.isBlank(groupId)) {
			throw new DataFormatException("The requested group ID" + projectId + " wasn't found ");
		}
	}

	private List<PermissionCriteriaI> getNewPermissions(UserGroupI working, Map<String, Object> groupProperties) throws InitializationException {
		final List<PermissionCriteriaI> newPermissions = new ArrayList<>();
		try {
		 final List<ElementSecurity> elements = ElementSecurity.GetSecureElements();

         for (final ElementSecurity element : elements) {
             final List<String> permissionItems = element.getPrimarySecurityFields();
             for (final String securityField : permissionItems) {
                 final PermissionCriteria criteria = new PermissionCriteria(element.getElementName());
                 criteria.setField(securityField);
                 criteria.setFieldValue(working.getTag());

                 final String elementId = element.getElementName() + "_" + securityField + "_" + working.getTag();
                 if (groupProperties.get(elementId + "_R") != null) {
                     criteria.setRead(true);
                 } else {
                     criteria.setRead(false);
                 }
                 if (groupProperties.get(elementId + "_E") != null && !StringUtils.equals(element.getElementName(), XnatProjectdata.SCHEMA_ELEMENT_NAME)) {
                     criteria.setRead(true);
                     criteria.setEdit(true);
                     criteria.setCreate(true);
                     criteria.setActivate(true);
                 } else {
                     criteria.setCreate(false);
                     criteria.setEdit(false);
                     criteria.setActivate(false);
                 }
                 if (groupProperties.get(elementId + "_D") != null && !StringUtils.equals(element.getElementName(), XnatProjectdata.SCHEMA_ELEMENT_NAME)) {
                     criteria.setRead(true);
                     criteria.setDelete(true);
                 } else {
                     criteria.setDelete(false);
                 }
                 criteria.setComparisonType("equals");

                 final boolean wasSet = StringUtils.equals((String) groupProperties.get(elementId + "_wasSet"), "1");

                 if (wasSet || criteria.getCreate() || criteria.getRead() || criteria.getEdit() || criteria.getDelete() || criteria.getActivate()) {
                     newPermissions.add(criteria);
                     //inherit project permissions to shared project permissions
                     if (StringUtils.equals(criteria.getField(), element.getElementName() + "/project") && (wasSet || criteria.getRead())) {
                         final PermissionCriteria share = new PermissionCriteria(element.getElementName());
                         share.setField(element.getElementName() + "/sharing/share/project");
                         share.setFieldValue(working.getTag());
                         share.setRead(criteria.getRead());
                         share.setComparisonType("equals");
                         newPermissions.add(share);
                     }
                 }
             }
         }
		} catch (Exception e) {
            log.error("", e);
            throw new InitializationException(e.getLocalizedMessage());
        }
		return newPermissions;
		
	}

	private UserGroupI findGroupByNameAndDisplayName(String groupName, String displayName, XnatProjectdata project) {
		if (StringUtils.isAllBlank(groupName, displayName)) {
            return null;
        }
        if (StringUtils.isNotBlank(groupName)) {
            if (NumberUtils.isCreatable(groupName)) {
                final UserGroupI group = Groups.getGroupByPK(groupName);
                if (group != null) {
                    return group;
                }
            }
            final UserGroupI byName = Groups.getGroup(groupName);
            if (byName != null) {
                return byName;
            }
            final UserGroupI byProjectAndGroupName = Groups.getGroup(project.getId() + "_" + groupName);
            if (byProjectAndGroupName != null) {
                return byProjectAndGroupName;
            }
            final UserGroupI byTagAndName = Groups.getGroupByTagAndName(project.getId(), groupName);
            if (byTagAndName != null) {
                return byTagAndName;
            }
        }
        if (StringUtils.isNotBlank(displayName)) {
            final UserGroupI byTagAndName = Groups.getGroupByTagAndName(project.getId(), displayName);
            if (byTagAndName != null) {
                return byTagAndName;
            }
            return Groups.getGroup(project.getId() + "_" + displayName);
        }
        return null;
	}

	private static class UserRowMapper implements RowMapper<XdatUsergroupI> {
		UserRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XdatUsergroupI mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final String userId = resultSet.getString("GROUP_ID");
			return XdatUsergroup.getXdatUsergroupsById(userId, _user, false);
		}

		private final UserI _user;
	}
	
	private static class UserGroupRowMapper implements RowMapper<XdatUsergroupI> {
		UserGroupRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XdatUsergroupI mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Integer userGroupId = resultSet.getInt("xdat_usergroup_id");
			return XdatUsergroup.getXdatUsergroupsByXdatUsergroupId(userGroupId, _user, false);
		}

		private final UserI _user;
	}
	
	private static class UserFavoriteRowMapper implements RowMapper<FavEntries> {

		@Override
		public FavEntries mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final String projectId = resultSet.getString("id");
			final String dataType = resultSet.getString("dataType");
			FavEntries favEntries = new FavEntries();
			favEntries.setDataType(dataType);
			favEntries.setId(projectId);
			return favEntries;
		}

	}
	
	private void validateDataType(String dataType) throws DataFormatException {
		if(StringUtils.isBlank(dataType)) {
    		throw new DataFormatException("The requested FavEntries for data Type" + dataType + " wasn't found ");
		}
	}
	
	private void ValidateProjectId(String projectId) throws DataFormatException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
	}

	
	private static final String USER_QUERY = "SELECT g.id AS GROUP_ID, displayname,login,firstname,lastname,email FROM xdat_userGroup g \n" + 
											  "RIGHT JOIN xdat_user_Groupid map ON g.id=map.groupid \n" + 
											  "RIGHT JOIN xdat_user u ON map.groups_groupid_xdat_user_xdat_user_id=u.xdat_user_id ";
	
	private static final String BY_ID_WHERE_PRO = "WHERE tag= :projectId  and enabled = 1  ORDER BY g.id DESC";
	
	private static final String BY_GROUP_ID_WHERE = " and ug.id= :groupId" ;
	
	private static final String USER_GROUP_QUERY = "SELECT ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id, \n" + 
			   										"COUNT(map.groups_groupid_xdat_user_xdat_user_id) AS users \n" + 
			   										"FROM xdat_userGroup ug \n" +
			   										"LEFT JOIN xdat_user_groupid map ON ug.id=map.groupid";

	private static final String BY_ID_WHERE_USER_GROUP_PROJECT = " WHERE tag= :projectId ";
	
	private static final String USER_GROUP_BY= " GROUP BY ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id ORDER BY ug.displayname DESC";
	
	private static final String BY_ID_DATATYPE_USERID_WHERE_USER_FAVORITE_QUERY = "SELECT datatype,id FROM xdat_search.xs_fav_entries WHERE dataType= :dataType AND id = :projectId AND xdat_user_id = :userId";
	
	private static final String BY_DATATYPE_USERID_WHERE_USER_FAVORITE_QUERY = "SELECT datatype,id FROM xdat_search.xs_fav_entries WHERE dataType= :dataType AND xdat_user_id = :userId";
	
	private static final List<String> PROTECTED_DISPLAY_NAMES = Arrays.asList("Owners", "Members", "Collaborators");
	
	private final NamedParameterJdbcTemplate _template;
	private final UserGroupServiceI _service;

}
