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
	public List<XnatAbstractresource> getResourceByExperimentAndScan(UserI user, String assessedId, String scanId) {
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
	
   private final NamedParameterJdbcTemplate _template;

}