package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatTemplateUtil;
import org.nrg.xnat.services.resources.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService{
	
	@Autowired
	public ResourceServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XnatAbstractresource> findByExperimentId(UserI user, String experimentId) {
		return _template.query(EXPERIMENT_QUERY + BY_ID_WHERE_EXPERIMENT, new MapSqlParameterSource("experimentId", experimentId), new ResourceRowMapper(user));
	}
	
	
	@Override
	public XnatAbstractresource findByIdAndExperimentId(UserI user, Integer resourceId, String experimentId) {
		return _template.queryForObject(EXPERIMENT_QUERY + BY_ID_WHERE_EXPERIMENT + AND_WHERE + BY_RESOURCE_ID_WHERE, new MapSqlParameterSource("resourceId", resourceId).addValue("experimentId", experimentId), new ResourceRowMapper(user));
	}

	@Override
	public List<XnatAbstractresource> findResourceByExperimentAndScan(UserI user, String assessedId, String scanId) {
		List<XnatAbstractresource> xnatAbstractresources;
		ArrayList<XnatExperimentdata> assesseds = XnatTemplateUtil.getXnatExperimentdata(assessedId, user,null);
		ArrayList<XnatImagescandata> scans = XnatTemplateUtil.getXnatImageScanData(scanId, user, assesseds);
		String query = XnatTemplateUtil.getQuery(scans,assesseds, null);
		 xnatAbstractresources = _template.query(query,  new ResourceRowMapper(user));
		 return xnatAbstractresources;
	}
	
	@Override
	public List<XnatAbstractresource> findByProject(UserI user, String projectId) {
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT, new MapSqlParameterSource("projectId", projectId), new ResourceRowMapper(user));
	}
	

	@Override
	public XnatAbstractresource findByIdAndProject(UserI user, Integer resourceId, String projectId) {
		return _template.queryForObject(PROJECT_QUERY + BY_ID_WHERE_PROJECT + AND_WHERE + BY_RESOURCE_ID_WHERE, new MapSqlParameterSource("resourceId", resourceId).addValue("projectId", projectId), new ResourceRowMapper(user));
	}
	
	@Override
	public List<XnatAbstractresource> findBySubject(UserI user, String subjectId) {
		return _template.query(SUBJECT_QUERY + BY_WHERE + BY_ID_WHERE_SUBJECT, new MapSqlParameterSource("subjectId", subjectId), new ResourceRowMapper(user));
	}
	
	@Override
	public List<XnatAbstractresource> findByProjectAndSubjectAndExperiment(UserI sessionUser, String projectId, String subjectId, String experimentId) {
		return null;
	}

	
	@Override
	public List<XnatAbstractresource> findByProjectAndSubject(UserI user, String projectId, String subjectId) {
		return _template.query(SUBJECT_QUERY + BY_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_SUBJECT  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
	}


	@Override
	public XnatAbstractresource findByIdAndProjectAndSubject(UserI user, Integer resourceId, String projectId, String subjectId) {
		return _template.queryForObject(SUBJECT_QUERY + BY_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_SUBJECT + AND_WHERE + BY_RESOURCE_ID_WHERE  , new MapSqlParameterSource("resourceId", resourceId).addValue("projectId", projectId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
	}
	
	@Override
	public XnatAbstractresource findByIdAndSubject(UserI user, Integer resourceId, String subjectId) {
		return _template.queryForObject(SUBJECT_QUERY  + BY_WHERE + BY_RESOURCE_ID_WHERE + AND_WHERE + BY_ID_WHERE_SUBJECT , new MapSqlParameterSource("resourceId", resourceId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
	}
	
	@Override
	public List<XnatAbstractresource> findResourceByexperimentIdAndAssessedId(UserI user, String experimentId, String assessedId ) {
		return _template.query(EXPERIMENT_ASSESSER_QUERY + BY_WHERE_EXP_ASSE   , new MapSqlParameterSource("experimentId", experimentId).addValue("assessedId", assessedId), new ResourceRowMapper(user));
	}
	
	@Override
	public XnatAbstractresource findResourceByexperimentIdAndAssessedIdAndResourceId(UserI user, String experimentId, String assessedId, Integer resourceId) {
		return _template.queryForObject(EXPERIMENT_ASSESSER_QUERY + BY_WHERE_EXP_ASSE + AND_WHERE + BY_WHERE_RESOURCE  , new MapSqlParameterSource("experimentId", experimentId).addValue("assessedId", assessedId).addValue("resourceId", resourceId), new ResourceRowMapper(user));	
	}
	
	@Override
	public List<XnatAbstractresource> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI user, String projectId, String subjectId, String experimentId, String assessedId) {
		return _template.query(PRO_SUB_EXP_ASS_QUERY + BY_WHERE_PRO_SUB_EXP_ASS  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId", experimentId).addValue("assessedId", assessedId), new ResourceRowMapper(user));
	}

	private static class ResourceRowMapper implements RowMapper<XnatAbstractresource> {
		ResourceRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public XnatAbstractresource mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final String xnatAbstractResourceId = resultSet.getString("xnat_abstractresource_id");
	        XnatAbstractresource xnatAbstractresource= XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(xnatAbstractResourceId, _user, false);
	        return xnatAbstractresource;
	    }
	    private final UserI _user;
	    
	}
	
	private static final String BY_ID_WHERE_EXPERIMENT = " where x.id = :experimentId ";
	
	private static final String BY_ID_WHERE_PROJECT = " where pr.xnat_projectdata_id = :projectId ";
	
	private static final String AND_WHERE = " and";
	
	private static final String BY_RESOURCE_ID_WHERE = " ar.xnat_abstractresource_id = :resourceId ";
	
	private static final String BY_ID_WHERE_SUBJECT = " s.id = :subjectId ";

	private static final String BY_WHERE = " where";
	
	private static final String BY_WHERE_PROJECT = " where s.project = :projectId ";
	
	private static final String PROJECT_QUERY = "SELECT  ar.xnat_abstractresource_id FROM xnat_abstractresource ar \n" +
												"LEFT JOIN xnat_projectdata_resource pr ON ar.xnat_abstractresource_id = pr.xnat_abstractresource_xnat_abstractresource_id";
	
	private static final String SUBJECT_QUERY = "SELECT DISTINCT ar.xnat_abstractresource_id FROM xnat_subjectdata s\n" + 
												" LEFT JOIN xnat_subjectdata_resource r ON s.id = r.xnat_subjectdata_id\n" + 
												" LEFT JOIN xnat_abstractresource ar ON ar.xnat_abstractresource_id = r.xnat_abstractresource_xnat_abstractresource_id\n" + 
												" LEFT JOIN xdat_meta_element e ON ar.extension = e.xdat_meta_element_id";
	
	private static final String EXPERIMENT_QUERY = "SELECT DISTINCT ar.xnat_abstractresource_id, e.element_name FROM  xnat_experimentdata x \n" + 
													" LEFT JOIN xnat_imagescandata s ON x.id = s.image_session_id\n" + 
													" LEFT JOIN xnat_experimentdata_resource r ON r.xnat_experimentdata_id = x.id\n" + 
													" LEFT JOIN img_assessor_in_resource air ON air.xnat_imageassessordata_id = x.id\n" + 
													" LEFT JOIN img_assessor_out_resource aor ON aor.xnat_imageassessordata_id = x.id\n" + 
													" LEFT JOIN xnat_imageassessordata a ON x.id = a.id\n" + 
													" LEFT JOIN xnat_abstractresource ar ON ar.xnat_imagescandata_xnat_imagescandata_id = s.xnat_imagescandata_id OR\n" + 
													" ar.xnat_abstractresource_id IN (r.xnat_abstractresource_xnat_abstractresource_id, air.xnat_abstractresource_xnat_abstractresource_id, aor.xnat_abstractresource_xnat_abstractresource_id)\n" + 
													" LEFT JOIN xdat_meta_element e ON ar.extension = e.xdat_meta_element_id "; 
	
	private static final String EXPERIMENT_ASSESSER_QUERY= "SELECT xnat_abstractresource_id\n" + 
																	"FROM img_assessor_out_resource map \n" + 
																	"LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id  \n" + 
																	"LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id \n" + 
																	"LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name \n" + 
																	"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
																	"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id \n" + 
																	"LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id\n" + 
																	"LEFT JOIN xnat_imageAssessorData iad ON map.xnat_imageassessordata_id=iad.id";
	
	private static final String BY_WHERE_EXP_ASSE = "  WHERE iad.imagesession_id= :experimentId  AND map.xnat_imageassessordata_id = :assessedId ";
  
	private static final String BY_WHERE_RESOURCE = " xnat_abstractresource_id = :resourceId";
	
	
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
	
	private static final String BY_WHERE_PRO_SUB_EXP_ASS= " WHERE expt.project = :projectId AND sad.subject_id= :subjectId AND iad.imagesession_id= :experimentId  AND map.xnat_imageassessordata_id = :assessedId ";
	
	private final NamedParameterJdbcTemplate _template;


}