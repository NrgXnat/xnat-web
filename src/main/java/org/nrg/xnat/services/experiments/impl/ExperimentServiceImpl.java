package org.nrg.xnat.services.experiments.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.experiments.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExperimentServiceImpl implements ExperimentService {
	
	@Autowired
	public ExperimentServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XnatExperimentdata> getAll(UserI user) {
		return XnatExperimentdata.getAllXnatExperimentdatas(user, false);
	}

	@Override
	public XnatExperimentdata findById(UserI user, String experimentId) {
		return XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
	}

	@Override
	public List<XnatExperimentdata> findByProject(UserI user, String projectId) {
		return _template.query(PROJECT_EXPERIMENT_QUERY, new MapSqlParameterSource("projectId", projectId),new ExperimentRowMapper(user));
	}

	@Override
	public List<XnatExperimentdata> findByProjectAndSubject(UserI user, String projectId, String subjectId) {
		return _template.query(PROJECT_SUBJECT_EXPERIMENT_QUERY, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId),new ExperimentRowMapper(user));
	}
	
	@Override
	public XnatExperimentdata findByIdAndProject(UserI user, String experimentId, String projectId) {
		return _template.queryForObject(PROJECT_AND_EXPERIMENT_QUERY + BY_PRO_EXP_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId).addValue("projectId", projectId),new ExperimentRowMapper(user));
	}
	
	@Override
	public XnatExperimentdata create(UserI user, XnatExperimentdata xnatExperimentdata) {
		return null;
	}
	
	@Override
	public XnatExperimentdata update(UserI user, XnatExperimentdata xnatExperimentdata, String experimentId) {
		return null;
	}

	@Override
	public void deleteById(UserI user, String experimentId) {
	}

	@Override
	public List<XnatExperimentdata> findByProjectAndLabel(UserI user, String projectId, String label) {
		return null;
	}
	
	
	private static class ExperimentRowMapper implements RowMapper<XnatExperimentdata> {
	    ExperimentRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public XnatExperimentdata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final String experimentId = resultSet.getString("id");
	        XnatExperimentdata xnatExperimentdata= XnatExperimentdata.getXnatExperimentdatasById(experimentId, _user, false);
	        return xnatExperimentdata;
	    }
	    private final UserI _user;
	}
	
	
	
	private static final String PROJECT_AND_EXPERIMENT_QUERY= "SELECT ed.id FROM xnat_experimentdata ed LEFT JOIN xnat_projectdata pd ON ed.project = pd.id ";
																
	private static final String BY_PRO_EXP_ID_WHERE = " WHERE ed.id= :experimentId AND pd.id = :projectId";
	
	private  final String PROJECT_EXPERIMENT_QUERY = EXPERIMENT_SUB_QUERY1 + BY_PRO_ID_WHERE1  +  EXPERIMENT_SUB_QUERY2 +  BY_PRO_ID_WHERE2 + EXPERIMENT_SUB_QUERY3;
	
	private  final String PROJECT_SUBJECT_EXPERIMENT_QUERY = EXPERIMENT_SUB_QUERY1 + BY_PRO_SUB_ID_WHERE  +  EXPERIMENT_SUB_QUERY2 +  BY_PRO_SUB_ID_WHERE2 + EXPERIMENT_SUB_QUERY3;
	
	
	private static final String BY_PRO_SUB_ID_WHERE ="    SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId))) AND (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId)))))";
	
	private static final String BY_PRO_SUB_ID_WHERE2 = "    SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId))) AND (( (xnat_experimentData14=:projectId) OR  (xnat_experimentData_share25= :projectId)) AND   \n" + 
			" ((xnat_subjectAssessorData1= :subjectId)))))";

	
	private static final String BY_PRO_ID_WHERE1 ="   SECURITY WHERE ((( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))) AND \n" + 
			" (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))))) ";
	
	
	private static final String BY_PRO_ID_WHERE2 ="    SECURITY WHERE ((( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))) AND \n" + 
			" (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))))) ";
	
	
	
	private static final String EXPERIMENT_SUB_QUERY1 =" SELECT table0.id AS id, xnat_experimentData.xnatSubjectAssessorDataId AS xnatSubjectAssessorDataId, \n" + 
			"xnat_experimentData.project AS project, xnat_experimentData.date AS date, xnat_experimentData.xsiType AS xsiType, \n" + 
			"xnat_experimentData.label AS label,xnat_experimentData.insertDate AS insertDate \n" + 
			"FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, \n" + 
			"table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS \n" + 
			"xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" + 
			"LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   \n" + 
			"LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)  ";
	
	private static final String EXPERIMENT_SUB_QUERY2 = " SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id \n" + 
			" LEFT JOIN (SELECT table0.id AS id FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)    " ;
	
	private static final String EXPERIMENT_SUB_QUERY3 = "  SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id) AS map_xnat_experimentData ON table0.id=map_xnat_experimentData.id \n" + 
			" LEFT JOIN (SELECT xnat_experimentData.id AS xnatSubjectAssessorDataId, xnat_experimentData.project AS project, xnat_experimentData.date AS date, table1.element_name AS xsiType, xnat_experimentData.label AS label, table2.insert_date AS insertDate   \n" + 
			" FROM xnat_experimentData xnat_experimentData   \n" + 
			" LEFT JOIN xdat_meta_element table1 ON xnat_experimentData.extension=table1.xdat_meta_element_id   \n" + 
			" LEFT JOIN xnat_experimentData_meta_data table2 ON xnat_experimentData.experimentData_info=table2.meta_data_id) AS xnat_experimentData ON map_xnat_experimentData.id=xnat_experimentData.xnatSubjectAssessorDataId";
	

	private final NamedParameterJdbcTemplate _template;


	

}
