package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.model.subjects.XnatProjectGroup;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.files.ProjectGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProjectGroupServiceImpl implements ProjectGroupService {

	@Autowired
	public ProjectGroupServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public XnatProjectGroup create(UserI user, XnatProjectGroup item) {
		return null;
	}

	@Override
	public List<XnatProjectGroup> getAll(UserI user) {
		return null;
	}

	@Override
	public XnatProjectGroup get(UserI user, int itemId) {
		return null;
	}

	@Override
	public XnatProjectGroup findById(UserI user, String itemId) {
		return null;
	}

	@Override
	public List<XnatProjectGroup> findProjectGroupByProjectId(UserI user, String projectId) {
		return _template.query(PROJECT_GROUP_QUERY, new MapSqlParameterSource("projectId", projectId), ROW_MAPPER);
	}

	private static final String BY_ID_WHERE = " WHERE ug.tag = :projectId";

	private static final String PROJECT_GROUP_QUERY = " SELECT ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id, "
			+ "COUNT(map.groups_groupid_xdat_user_xdat_user_id) AS users " + "FROM xdat_userGroup ug "
			+ "LEFT JOIN xdat_user_groupid map ON ug.id=map.groupid " +    BY_ID_WHERE + "   "
			+ "GROUP BY ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id  " + "ORDER BY ug.displayname DESC";

	private static final RowMapper<XnatProjectGroup> ROW_MAPPER = new RowMapper<XnatProjectGroup>() {
		@Override
		public XnatProjectGroup mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatProjectGroup(resultSet);
		}
	};

	private final NamedParameterJdbcTemplate _template;

}
