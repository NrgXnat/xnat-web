package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.model.subjects.XnatProject;
import org.nrg.xapi.model.subjects.XnatSubject;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.ProjectListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProjectListServiceImpl implements ProjectListService {

	@Autowired
	public ProjectListServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public XnatProject create(UserI user, XnatProject item) {
		return null;
	}

	@Override
	public List<XnatProject> getAll(UserI user) {
		return _template.query(SUBJECT_QUERY, ROW_MAPPER);
	}

	@Override
	public XnatProject get(UserI user, int itemId) {
		return null;
	}

	@Override
	public XnatProject findById(UserI user, String projectId) {
		return _template.queryForObject(SUBJECT_QUERY + BY_ID_WHERE, new MapSqlParameterSource("projectId", projectId),
				ROW_MAPPER);
	}

	private static final String BY_ID_WHERE = " WHERE xnat_projectData.id = :projectId";

	private static final String SUBJECT_QUERY = " SELECT xnat_projectData.id AS id, xnat_projectData.secondary_id AS secondaryId, \n"
			+ "xnat_projectData.name AS name, xnat_projectData.description AS description, \n"
			+ "table1.firstname AS piFirstName, table1.lastname AS piLastName\n" + "from xnat_projectData\n"
			+ "LEFT JOIN xnat_investigatorData table1 ON xnat_projectData.pi_xnat_investigatordata_id=table1.xnat_investigatordata_id";

	private static final RowMapper<XnatProject> ROW_MAPPER = new RowMapper<XnatProject>() {
		@Override
		public XnatProject mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatProject(resultSet);
		}
	};

	private final NamedParameterJdbcTemplate _template;
}
