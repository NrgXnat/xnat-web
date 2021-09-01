package org.nrg.xnat.services.users.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.UserGroupI;
import org.nrg.xdat.security.UserGroupServiceI;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.UserHelper;
import org.nrg.xft.db.FavEntries;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserService;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
	public List<XdatUsergroup> findByProject(UserI user, String projectId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		List<XdatUsergroup> users = _template.query(USER_QUERY + BY_ID_WHERE_PRO, new MapSqlParameterSource("projectId", projectId), new UserRowMapper(user));
		if(Objects.isNull(users) || users.isEmpty()) {
    		throw new  NotFoundException(XdatUsergroup.SCHEMA_ELEMENT_NAME, projectId) ;
		}
    	return users;
	}
	
	@Override
	public List<XdatUsergroup> findUserGroupByProject(UserI user, String projectId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		List<XdatUsergroup> userGroups = _template.query(USER_GROUP_QUERY + BY_ID_WHERE_USER_GROUP_PROJECT + USER_GROUP_BY, new MapSqlParameterSource("projectId", projectId), new UserGroupRowMapper(user));
		if(Objects.isNull(userGroups) || userGroups.isEmpty()) {
    		throw new  NotFoundException(XdatUsergroup.SCHEMA_ELEMENT_NAME, projectId) ;
		}
    	return userGroups;
	}
	
	@Override
	public Optional<XdatUsergroup> findUserGroupByGroupIdAndProject(UserI user, String groupId, String projectId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		if(StringUtils.isBlank(groupId)) {
    		throw new DataFormatException("The requested group ID" + projectId + " wasn't found ");
		}
		XdatUsergroup userGroup = _template.queryForObject(USER_GROUP_QUERY + BY_ID_WHERE_USER_GROUP_PROJECT + BY_GROUP_ID_WHERE + USER_GROUP_BY, new MapSqlParameterSource("projectId", projectId).addValue("groupId", groupId), new UserGroupRowMapper(user));
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
		if (StringUtils.isBlank(projectId)) {
			throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		if (StringUtils.isBlank(groupId)) {
			throw new DataFormatException("The requested group ID" + projectId + " wasn't found ");
		}
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

	private static class UserRowMapper implements RowMapper<XdatUsergroup> {
		UserRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XdatUsergroup mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final String userId = resultSet.getString("GROUP_ID");
			XdatUsergroup xnatSubjectdata = XdatUsergroup.getXdatUsergroupsById(userId, _user, false);
			return xnatSubjectdata;
		}

		private final UserI _user;
	}
	
	private static class UserGroupRowMapper implements RowMapper<XdatUsergroup> {
		UserGroupRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XdatUsergroup mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Integer userGroupId = resultSet.getInt("xdat_usergroup_id");
			XdatUsergroup xnatSubjectdata = XdatUsergroup.getXdatUsergroupsByXdatUsergroupId(userGroupId, _user, false);
			return xnatSubjectdata;
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
