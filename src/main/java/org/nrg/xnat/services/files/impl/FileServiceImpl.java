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
	

	private static final String BY_WHERE = " where";
	
	private static final String AND_WHERE = " and";
	
	private static final String BY_ID_WHERE_PROJECT = " where pr.xnat_projectdata_id = :projectId ";
	
	private static final String PROJECT_QUERY=  "SELECT xnat_abstractresource_id FROM xnat_projectdata_resource pr \n" + 
												"LEFT JOIN xnat_abstractresource abst ON pr.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
												"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id";
	
	private static final String SUBJECT_QUERY= "SELECT xnat_abstractresource_id FROM xnat_subjectdata_resource map \n" + 
												"LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id \n" + 
												"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
												"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id ";
	
	private static final String BY_ID_WHERE_PROJ = " where sub.project = :projectId ";
	
	private static final String BY_ID_WHERE_SUBJECT = " xnat_subjectdata_id = :subjectId ";
	
	private final NamedParameterJdbcTemplate _template;

}
