package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.model.subjects.XnatSubject;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.SubjectListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubjectListServiceImpl implements SubjectListService {

	@Autowired
	public SubjectListServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public XnatSubject create(UserI user, XnatSubject item) {
		return null;
	}

	@Override
	public List<XnatSubject> getAll(UserI user) {
		return _template.query(SUBJECT_QUERY, ROW_MAPPER);
	}

	@Override
	public XnatSubject get(UserI user, int itemId) {
		return null;
	}

	@Override
	public XnatSubject findById(UserI user, String subjectId) {
		return _template.queryForObject(SUBJECT_QUERY + BY_ID_WHERE, new MapSqlParameterSource("subjectId", subjectId),ROW_MAPPER);
	}
	
	@Override
	public List<XnatSubject> findSubjectsByProjectId(UserI sessionUser, String projectId) {
		return _template.query(SUBJECT_QUERY + BY_ID_WHERE_PRO_SUB, new MapSqlParameterSource("projectId", projectId),ROW_MAPPER);
	}
	
	

	private static final String BY_ID_WHERE = " WHERE xnat_subjectData.id = :subjectId";
	
	private static final String BY_ID_WHERE_PRO_SUB = " WHERE xnat_subjectData.project = :projectId";

	private static final String SUBJECT_QUERY = " SELECT xnat_subjectData.id AS id, xnat_subjectData.project AS project, xnat_subjectData.label AS label,\n"
			+ " table1.insert_date AS insertDate, table2.login AS insertUser\n" + "FROM xnat_subjectData\n"
			+ "LEFT JOIN xnat_subjectData_meta_data table1 ON xnat_subjectData.subjectData_info=table1.meta_data_id \n"
			+ "LEFT JOIN xdat_user table2 ON table1.insert_user_xdat_user_id=table2.xdat_user_id";

	private static final RowMapper<XnatSubject> ROW_MAPPER = new RowMapper<XnatSubject>() {
		@Override
		public XnatSubject mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatSubject(resultSet);
		}
	};

	private final NamedParameterJdbcTemplate _template;

}
