package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.model.subjects.XnatExperiment;
import org.nrg.xapi.model.subjects.XnatProjSubExperiment;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.ExperimentListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExperimentListServiceImpl implements ExperimentListService {

	@Autowired
	public ExperimentListServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public XnatExperiment create(UserI user, XnatExperiment item) {
		return null;
	}

	@Override
	public List<XnatExperiment> getAll(UserI user) {
		return _template.query(EXPERIMENT_QUERY, ROW_MAPPER);
	}

	@Override
	public XnatExperiment get(UserI user, int itemId) {
		return null;
	}

	@Override
	public XnatExperiment findById(UserI user, String experimentId) {
		return _template.queryForObject(EXPERIMENT_QUERY + BY_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId),ROW_MAPPER);
	}
	
	
	@Override
	public List<XnatProjSubExperiment> findAllExprimentByProjectIdAndSubjectId(UserI user, String projectId, String subjectId) {
		 return _template.query(PROJECT_SUBJECT_EXPERIMENT_QUERY, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId),PRO_SUB_ROW_MAPPER);
	}

	private static final String BY_ID_WHERE = " WHERE xnat_experimentData.id = :experimentId";

	private static final String EXPERIMENT_QUERY = "SELECT xnat_experimentData.id AS id, xnat_experimentData.project AS project, \n" + 
			"xnat_experimentData.date AS date, xdat_meta_ele.element_name AS xsiType, \n" + 
			"xnat_experimentData.label AS label, \n" + 
			"xnat_exp_meta_data.insert_date AS insertDate FROM xnat_experimentData xnat_experimentData   \n" + 
			"LEFT JOIN xdat_meta_element xdat_meta_ele ON xnat_experimentData.extension=xdat_meta_ele.xdat_meta_element_id   \n" + 
			"LEFT JOIN xnat_experimentData_meta_data xnat_exp_meta_data ON xnat_experimentData.experimentData_info=xnat_exp_meta_data.meta_data_id";

	private static final RowMapper<XnatExperiment> ROW_MAPPER = new RowMapper<XnatExperiment>() {
		@Override
		public XnatExperiment mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatExperiment(resultSet);
		}
	};
	
	private static final RowMapper<XnatProjSubExperiment> PRO_SUB_ROW_MAPPER = new RowMapper<XnatProjSubExperiment>() {
		@Override
		public XnatProjSubExperiment mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatProjSubExperiment(resultSet);
		}
	};
	

	private static final String BY_PRO_SUB_ID_WHERE ="  SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId))) AND (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId)))))";
	
	private static final String BY_PRO_SUB_ID_WHERE2 = "  SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId))) AND (( (xnat_experimentData14=:projectId) OR  (xnat_experimentData_share25= :projectId)) AND   \n" + 
			" ((xnat_subjectAssessorData1= :subjectId)))))";
	
	private static final String PROJECT_SUBJECT_EXPERIMENT_QUERY =" SELECT table0.id AS id, xnat_experimentData.xnatSubjectAssessorDataId AS xnatSubjectAssessorDataId, \n" + 
			"xnat_experimentData.project AS project, xnat_experimentData.date AS date, xnat_experimentData.xsiType AS xsiType, \n" + 
			"xnat_experimentData.label AS label,xnat_experimentData.insertDate AS insertDate \n" + 
			"FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, \n" + 
			"table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS \n" + 
			"xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" + 
			"LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   \n" + 
			"LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)  \n"  + 
			  BY_PRO_SUB_ID_WHERE + "  \n" +
			" SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id \n" + 
			" LEFT JOIN (SELECT table0.id AS id FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)   \n" + 
			  BY_PRO_SUB_ID_WHERE2 + "  \n" +
			" SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id) AS map_xnat_experimentData ON table0.id=map_xnat_experimentData.id \n" + 
			" LEFT JOIN (SELECT xnat_experimentData.id AS xnatSubjectAssessorDataId, xnat_experimentData.project AS project, xnat_experimentData.date AS date, table1.element_name AS xsiType, xnat_experimentData.label AS label, table2.insert_date AS insertDate   \n" + 
			" FROM xnat_experimentData xnat_experimentData   \n" + 
			" LEFT JOIN xdat_meta_element table1 ON xnat_experimentData.extension=table1.xdat_meta_element_id   \n" + 
			" LEFT JOIN xnat_experimentData_meta_data table2 ON xnat_experimentData.experimentData_info=table2.meta_data_id) AS xnat_experimentData ON map_xnat_experimentData.id=xnat_experimentData.xnatSubjectAssessorDataId";

	private final NamedParameterJdbcTemplate _template;

}
