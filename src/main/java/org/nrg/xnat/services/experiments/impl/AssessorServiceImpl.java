package org.nrg.xnat.services.experiments.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.experiments.AssessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AssessorServiceImpl implements AssessorService {
	
	@Autowired
	public AssessorServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XnatImageassessordata> findByProjectAndSubjectAndExperiment(UserI user, String projectId, String subjectId, String experimentId) {
		return _template.query(PROJECT_SUBJECT_AND_EXPERIMENT_QUERY + BY_PRO_SUB_EXP_ID_WHERE, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId", experimentId),new AssessorRowMapper(user));
	}
	
	@Override
	public XnatImageassessordata findByIdAndProjectAndSubjectAndExperiment(UserI user, String projectId, String subjectId, String experimentId, String assessorId) {
		return _template.queryForObject(PROJECT_SUBJECT_AND_EXPERIMENT_QUERY + BY_PRO_SUB_EXP_ID_WHERE + BY_ASS_ID_WHERE, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId", experimentId).addValue("assessorId", assessorId),new AssessorRowMapper(user));
	}
	
	private static class AssessorRowMapper implements RowMapper<XnatImageassessordata> {
		AssessorRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public XnatImageassessordata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final String assessorId = resultSet.getString("id");
	        XnatImageassessordata xnatImageassessordata = XnatImageassessordata.getXnatImageassessordatasById(assessorId, _user, false);
	        return xnatImageassessordata;
	    }
	    private final UserI _user;
	}
	
	private static final String PROJECT_SUBJECT_AND_EXPERIMENT_QUERY = "SELECT  DISTINCT  iad.id AS id from xnat_imageSessionData isd\n" + 
																		" LEFT JOIN xnat_imageAssessorData iad ON isd.id=iad.imageSession_ID\n" + 
																		" LEFT JOIN xnat_derivedData dd ON isd.id=dd.id \n" + 
																		" LEFT JOIN xnat_experimentData ed ON dd.id=ed.id \n" + 
																		" LEFT JOIN xnat_subjectAssessorData sad ON isd.id=sad.id \n" + 
																		" LEFT JOIN xnat_subjectdata sd ON sd.id=sad.subject_id\n" + 
																		" LEFT JOIN xnat_experimentData ed1 ON sd.project=ed1.project \n" + 
																		"LEFT JOIN xnat_experimentData_share eds ON ed.id=eds.sharing_share_xnat_experimentDa_id ";
	
	private static final String BY_PRO_SUB_EXP_ID_WHERE = " where iad.imagesession_id= :experimentId AND ed1.project = :projectId \n" + 
														 " AND sad.subject_id= :subjectId ";
	
	private static final String BY_ASS_ID_WHERE = " AND iad.id = :assessorId";
	
	private final NamedParameterJdbcTemplate _template;

}
