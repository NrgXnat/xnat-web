package org.nrg.xnat.services.files.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	public FileServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}
	
	@Override
	public List<XnatResourcecatalog> findByProject(UserI user, String projectId) {
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT, new MapSqlParameterSource("projectId", projectId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findBySubject(UserI user, String subjectId) {
		return _template.query(SUBJECT_QUERY + BY_WHERE + BY_ID_WHERE_SUBJECT, new MapSqlParameterSource("subjectId", subjectId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByProjectAndSubject(UserI user, String projectId, String subjectId) {
		return _template.query(SUBJECT_QUERY + BY_ID_WHERE_PROJ + AND_WHERE + BY_ID_WHERE_SUBJECT , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByProjectAndResource(UserI user, String projectId, Integer resourceId) {
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJ_AND_RESOURCE, new MapSqlParameterSource("projectId", projectId).addValue("resourceId", resourceId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findBySubjectAndResource(UserI user, String subjectId, Integer resourceId) {
		return _template.query(SUBJECT_RESOURCE_QUERY + BY_ID_WHERE_SUBJ_AND_RESOURCE, new MapSqlParameterSource("subjectId", subjectId).addValue("resourceId", resourceId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByExperimentAndAssessors(UserI user, String experimentId, String assessorId) {
		return _template.query(EXPERIMENT_ASSESSER_QUERY + BY_ID_WHERE_EXP_AND_ASSESSER, new MapSqlParameterSource("experimentId", experimentId).addValue("assessorId", assessorId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI user,String projectId, String subjectId, String experimentId, String assessedId) {
		return _template.query(PRO_SUB_EXP_ASS_QUERY + BY_WHERE_PRO_SUB_EXP_ASS  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId", experimentId).addValue("assessedId", assessedId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByExperiment(UserI user, String experimentId) {
		return _template.query(EXP_FILE_QUERY, new MapSqlParameterSource("experimentId", experimentId), new FileRowMapper(user));
	}

	@Override
	public List<XnatResourcecatalog> findByExperimentAndResource(UserI user, String experimentId, Integer resourceId) {
		return _template.query(EXP_RESOURCE_QUERY, new MapSqlParameterSource("experimentId", experimentId).addValue("resourceId", resourceId), new FileRowMapper(user));
	}
	
	private static class FileRowMapper implements RowMapper<XnatResourcecatalog> {
		FileRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public XnatResourcecatalog mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final String xnatAbstractResourceId = resultSet.getString("xnat_abstractresource_id");
	        XnatResourcecatalog xnatResourcecatalogs= XnatResourcecatalog.getXnatResourcecatalogsByXnatAbstractresourceId(xnatAbstractResourceId, _user, false);
	        return xnatResourcecatalogs;
	    }
	    private final UserI _user;
	    
	}
	
	
	private static final String PROJECT_QUERY=  "SELECT xnat_abstractresource_id FROM xnat_projectdata_resource pr \n" + 
												"LEFT JOIN xnat_abstractresource abst ON pr.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
												"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id";
	
	private static final String SUBJECT_QUERY= "SELECT xnat_abstractresource_id FROM xnat_subjectdata_resource map \n" + 
												"LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id \n" + 
												"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
												"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id ";
	
	private static final String SUBJECT_RESOURCE_QUERY= "SELECT xnat_abstractresource_id\n" + 
														"FROM xnat_subjectdata_resource map \n" + 
														"LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id \n" + 
														"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
														"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id";
	
	private static final String EXPERIMENT_ASSESSER_QUERY = "SELECT xnat_abstractresource_id\n" + 
															"FROM img_assessor_out_resource map \n" + 
															"LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id  \n" + 
															"LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id \n" + 
															"LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name \n" + 
															"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
															"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id \n" + 
															"LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id\n" + 
															"LEFT JOIN xnat_imageAssessorData iad ON map.xnat_imageassessordata_id=iad.id";
	
	private static final String PRO_SUB_EXP_ASS_QUERY=" SELECT xnat_abstractresource_id\n" + 
			  											"FROM img_assessor_out_resource map \n" + 
			  											"LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id  \n" + 
			  											"LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id \n" + 
			  											"LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name \n" + 
			  											"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
			  											"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id \n" + 
			  											"LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id\n" + 
			  											"LEFT JOIN xnat_imageAssessorData iad ON map.xnat_imageassessordata_id=iad.id\n" + 
			  											"LEFT JOIN xnat_subjectAssessorData sad ON iad.imagesession_id=sad.id  \n" + 
			  											"LEFT JOIN xnat_subjectdata sd ON sd.id=sad.subject_id ";
	
	
	private static final String EXP_FILE_RESOURCE_QUERY_1  = " SELECT xnat_abstractresource_id FROM xnat_experimentdata_resource res_map \n" + 
	   											  "  JOIN xnat_abstractresource abst  ON res_map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
	   											  "  JOIN  xdat_meta_element xme  ON abst.extension = xme.xdat_meta_element_id      " ;
	
	
	private static final String EXP_FILE_RESOURCE_QUERY_2 =  " SELECT xnat_abstractresource_id FROM xnat_imagescanData isd \n" + 
				  									" JOIN  xnat_abstractresource abst ON isd.xnat_imagescandata_id = abst.xnat_imagescandata_xnat_imagescandata_id \n" + 
				  									" JOIN  xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id      " ;
	
	private static final String EXP_FILE_RESOURCE_QUERY_3 = " SELECT xnat_abstractresource_id FROM  xnat_imageassessordata iad \n" + 
												   " JOIN img_assessor_out_resource map  ON iad.id = map.xnat_imageassessordata_id \n" + 
												   " JOIN xnat_abstractresource abst  ON map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
												   " JOIN xdat_meta_element xme  ON abst.extension = xme.xdat_meta_element_id \n" + 
												   " LEFT JOIN xdat_element_security xes  ON xme.element_name = xes.element_name       " ;
	
	private static final String EXP_FILE_RESOURCE_QUERY_4 =  " SELECT  xnat_abstractresource_id  FROM xnat_imageassessordata  iad \n" + 
													" JOIN xnat_experimentdata_resource map  ON iad.id = map.xnat_experimentdata_id \n" + 
													" JOIN  xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
													" JOIN  xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id \n" + 
													" LEFT JOIN xdat_element_security xes   ON xme.element_name = xes.element_name     " ;
	
	
	private final String EXP_FILE_QUERY = EXP_FILE_RESOURCE_QUERY_1 + BY_WHERE +  BY_ID_WHERE_EXP_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_2
			+ BY_WHERE + BY_ID_WHERE_EXP_ID_IMG_SESSION_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_3 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID
			+ BY_UNION + EXP_FILE_RESOURCE_QUERY_4 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID;

	private final String EXP_RESOURCE_QUERY = EXP_FILE_RESOURCE_QUERY_1 + BY_WHERE + BY_ID_WHERE_EXP_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_2
				+ BY_WHERE + BY_ID_WHERE_EXP_ID_IMG_SESSION_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_3 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID
				+ BY_UNION + EXP_FILE_RESOURCE_QUERY_4 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID;

	
	private static final String BY_WHERE = " where";
	
	private static final String AND_WHERE = " and  ";
	
	private static final String BY_UNION = "  UNION ";
	
	private static final String BY_ID_WHERE_PROJECT = " where pr.xnat_projectdata_id = :projectId ";
	
	private static final String BY_WHERE_PRO_SUB_EXP_ASS= " WHERE expt.project = :projectId AND sad.subject_id= :subjectId AND iad.imagesession_id= :experimentId  AND map.xnat_imageassessordata_id = :assessedId ";
	
	private static final String BY_ID_WHERE_EXP_AND_ASSESSER = " WHERE iad.imagesession_id= :experimentId  AND  map.xnat_imageassessordata_id = :assessorId";
	
	private static final String BY_ID_WHERE_SUBJ_AND_RESOURCE = " WHERE xnat_subjectdata_id= :subjectId  AND map.xnat_abstractresource_xnat_abstractresource_id= :resourceId";
	
	private static final String BY_ID_WHERE_PROJ = " where sub.project = :projectId ";
	
	private static final String BY_ID_WHERE_PROJ_AND_RESOURCE = " WHERE xnat_projectdata_id= :projectId  AND pr.xnat_abstractresource_xnat_abstractresource_id = :resourceId ";
	
	private static final String BY_ID_WHERE_SUBJECT = " xnat_subjectdata_id = :subjectId ";
	
	private static final String BY_ID_WHERE_EXP_ID = " res_map.xnat_experimentdata_id = :experimentId  ";
	
	private static final String BY_ID_WHERE_EXP_ID_IMG_SESSION_ID = " isd.image_session_id = :experimentId  ";
	
	private static final String BY_ID_WHERE_EXP_ID_IMAGESESSION_ID = " iad.imagesession_id = :experimentId  ";
	
	private static final String BY_ID_WHERE_RESOURCE_ID  = "  abst.xnat_abstractresource_id = :resourceId ";

	private final NamedParameterJdbcTemplate _template;

}
