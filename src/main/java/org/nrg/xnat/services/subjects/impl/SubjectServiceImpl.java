package org.nrg.xnat.services.subjects.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.subjects.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl implements SubjectService {

	@Autowired
	public SubjectServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XnatSubjectdata> getAll(UserI user) {
		return XnatSubjectdata.getAllXnatSubjectdatas(user, false);

	}

	@Override
	public XnatSubjectdata findById(UserI user, String subjectId) {
		return XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
	}

	@Override
	public XnatSubjectdata findByProjectAndSubject(UserI user, String projectId, String subjectId) {
		return _template.queryForObject(SUBJECT_QUERY + BY_ID_WHERE_PRO + BY_ID_WHERE_SUB, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new SubjectRowMapper(user));
	}

	@Override
	public List<XnatSubjectdata> findByProject(UserI user, String projectId) {
		return _template.query(SUBJECT_QUERY + BY_ID_WHERE_PRO, new MapSqlParameterSource("projectId", projectId), new SubjectRowMapper(user));
	}

	@Override
	public XnatSubjectdata create(UserI user, XnatSubjectdata xnatSubjectdata) {
		return null;
	}

	@Override
	public XnatSubjectdata update(UserI user, XnatSubjectdata xnatSubjectdata, String subjectId) {
		return null;
	}

	@Override
	public void deleteById(UserI user, String subjectId) {

	}

	private static class SubjectRowMapper implements RowMapper<XnatSubjectdata> {

		SubjectRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XnatSubjectdata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final String subjectId = resultSet.getString("id");
			XnatSubjectdata xnatSubjectdata = XnatSubjectdata.getXnatSubjectdatasById(subjectId, _user, false);
			return xnatSubjectdata;
		}

		private final UserI _user;
	}

	private static final String BY_ID_WHERE_PRO = " WHERE xnat_subjectData.project = :projectId";
	
	private static final String BY_ID_WHERE_SUB = "  and  xnat_subjectData.id = :subjectId";

	private static final String SUBJECT_QUERY = " SELECT xnat_subjectData.id AS id, xnat_subjectData.project AS project, xnat_subjectData.label AS label,\n"
			+ " table1.insert_date AS insertDate, table2.login AS insertUser\n" + "FROM xnat_subjectData\n"
			+ "LEFT JOIN xnat_subjectData_meta_data table1 ON xnat_subjectData.subjectData_info=table1.meta_data_id \n"
			+ "LEFT JOIN xdat_user table2 ON table1.insert_user_xdat_user_id=table2.xdat_user_id";

	private final NamedParameterJdbcTemplate _template;
}
