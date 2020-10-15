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
		 return _template.queryForObject(SUBJECT_QUERY + BY_ID_WHERE, new MapSqlParameterSource("subjectId", subjectId), ROW_MAPPER);
	}

	
	 private static final String SUBJECT_QUERY  = "SELECT  s.id  AS id, s.label AS lable FROM xnat_subjectdata s";
	 private static final String BY_ID_WHERE  =" WHERE s.id = :subjectId";

	private static final RowMapper<XnatSubject> ROW_MAPPER = new RowMapper<XnatSubject>() {
		@Override
		public XnatSubject mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatSubject(resultSet);
		}
	};
	
	private final NamedParameterJdbcTemplate _template;
}
