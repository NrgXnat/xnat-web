package org.nrg.xnat.services.users.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	public UserServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}
	@Override
	public List<XdatUsergroup> findByProject(UserI user, String projectId) {
		return _template.query(USER_QUERY + BY_ID_WHERE_PRO, new MapSqlParameterSource("projectId", projectId), new UserRowMapper(user));
	}
	
	@Override
	public List<XdatUsergroup> getUserGroupByProject(UserI user, String projectId) {
		return _template.query(USER_GROUP_QUERY + BY_ID_WHERE_USER_GROUP_PROJECT, new MapSqlParameterSource("projectId", projectId), new UserGroupRowMapper(user));
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
			final String userGroupId = resultSet.getString("xdat_usergroup_id");
			XdatUsergroup xnatSubjectdata = XdatUsergroup.getXdatUsergroupsByXdatUsergroupId(userGroupId, _user, false);
			return xnatSubjectdata;
		}

		private final UserI _user;
	}

	
	private static final String USER_QUERY = "SELECT g.id AS GROUP_ID, displayname,login,firstname,lastname,email FROM xdat_userGroup g \n" + 
											  "RIGHT JOIN xdat_user_Groupid map ON g.id=map.groupid \n" + 
											  "RIGHT JOIN xdat_user u ON map.groups_groupid_xdat_user_xdat_user_id=u.xdat_user_id ";
	
	private static final String BY_ID_WHERE_PRO = "WHERE tag= :projectId  and enabled = 1  ORDER BY g.id DESC";
	
	private static final String USER_GROUP_QUERY = "SELECT ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id, \n" + 
			   										"COUNT(map.groups_groupid_xdat_user_xdat_user_id) AS users \n" + 
			   										"FROM xdat_userGroup ug \n" +
			   										"LEFT JOIN xdat_user_groupid map ON ug.id=map.groupid";

	private static final String BY_ID_WHERE_USER_GROUP_PROJECT = " WHERE tag= :projectId GROUP BY ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id ORDER BY ug.displayname DESC";
	
	private final NamedParameterJdbcTemplate _template;
}
