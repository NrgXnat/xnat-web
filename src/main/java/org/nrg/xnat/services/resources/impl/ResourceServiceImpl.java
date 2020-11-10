package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xft.security.UserI;
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
		return _template.query(EXPERIMENT_QUERY + BY_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId), new ResourceRowMapper(user));
	}
	
	
	@Override
	public XnatAbstractresource findByIdAndExperimentId(UserI user, Integer resourceId, String experimentId) {
		return _template.queryForObject(EXPERIMENT_QUERY + BY_ID_WHERE + AND_WHERE + BY_RESOURCE_ID_WHERE, new MapSqlParameterSource("resourceId", resourceId).addValue("experimentId", experimentId), new ResourceRowMapper(user));
	}

	@Override
	public List<XnatAbstractresource> findByProjectAndSubjectAndExperiment(UserI sessionUser, String projectId, String subjectId, String experimentId) {
		return null;
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
	
	private static final String BY_ID_WHERE = " where x.id = :experimentId ";
	
	private static final String AND_WHERE = " and";
	
	private static final String BY_RESOURCE_ID_WHERE = " ar.xnat_abstractresource_id = :resourceId ";
	
	private static final String EXPERIMENT_QUERY = "SELECT DISTINCT ar.xnat_abstractresource_id, e.element_name FROM  xnat_experimentdata x \n" + 
													" LEFT JOIN xnat_imagescandata s ON x.id = s.image_session_id\n" + 
													" LEFT JOIN xnat_experimentdata_resource r ON r.xnat_experimentdata_id = x.id\n" + 
													" LEFT JOIN img_assessor_in_resource air ON air.xnat_imageassessordata_id = x.id\n" + 
													" LEFT JOIN img_assessor_out_resource aor ON aor.xnat_imageassessordata_id = x.id\n" + 
													" LEFT JOIN xnat_imageassessordata a ON x.id = a.id\n" + 
													" LEFT JOIN xnat_abstractresource ar ON ar.xnat_imagescandata_xnat_imagescandata_id = s.xnat_imagescandata_id OR\n" + 
													" ar.xnat_abstractresource_id IN (r.xnat_abstractresource_xnat_abstractresource_id, air.xnat_abstractresource_xnat_abstractresource_id, aor.xnat_abstractresource_xnat_abstractresource_id)\n" + 
													" LEFT JOIN xdat_meta_element e ON ar.extension = e.xdat_meta_element_id "; 
			
	
//	private final String EXPERIMENT_QUERY = EXPERIMENT_sub_QUERY_1 + EXPERIMENT_sub_QUERY_2;
//	
//	private static final String BY_ID_WHERE_RES_MAP = " WHERE res_map.xnat_experimentdata_id = :experimentId";
//	
//	private static final String BY_ID_WHERE_ISD_IMAGE = " WHERE isd.image_session_id = :experimentId";
//			
//	private static final String EXPERIMENT_sub_QUERY_1 = "SELECT * FROM (SELECT xnat_abstractresource_id, abst.label, xme.element_name,'resources'::TEXT AS category, NULL::TEXT AS cat_id, ''::TEXT AS cat_desc \n" + 
//														  "FROM xnat_experimentdata_resource res_map \n" + 
//														  "JOIN xnat_abstractresource abst ON res_map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
//														  "JOIN xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id \n" + 
//														   BY_ID_WHERE_RES_MAP + " \n" ;
//			
//   private static final String EXPERIMENT_sub_QUERY_2 = "UNION SELECT xnat_abstractresource_id, abst.label, xme.element_name, 'scans'::TEXT, isd.id, isd.type \n" + 
//													     "FROM xnat_imagescanData isd \n" + 
//													     "JOIN  xnat_abstractresource abst  ON isd.xnat_imagescandata_id = abst.xnat_imagescandata_xnat_imagescandata_id \n" + 
//													     "JOIN xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id\n  "     + 
//													     BY_ID_WHERE_ISD_IMAGE + " ) all_resources";
	
   
   private final NamedParameterJdbcTemplate _template;

}
