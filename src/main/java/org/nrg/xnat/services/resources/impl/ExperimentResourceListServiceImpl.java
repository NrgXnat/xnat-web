package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.model.subjects.XnatExperimentResource;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.ExperimentResourceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExperimentResourceListServiceImpl implements ExperimentResourceListService {

	@Autowired
	public ExperimentResourceListServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public XnatExperimentResource create(UserI user, XnatExperimentResource item) {
		return null;
	}

	@Override
	public List<XnatExperimentResource> getAll(UserI user) {
		return null;
	}

	@Override
	public XnatExperimentResource get(UserI user, int itemId) {
		return null;
	}

	@Override
	public List<XnatExperimentResource> findResourceByExperimentId(UserI user, String experimentId) {
		return _template.query(EXPERIMENT_QUERY , new MapSqlParameterSource("experimentId", experimentId), ROW_MAPPER);
	}
	
	
	private static final String BY_ID_WHERE = " WHERE res_map.xnat_experimentdata_id = :experimentId";
	//private static final String BY_ID_WHERE = " WHERE res_map.xnat_experimentdata_id IN ( 'XNAT_E00008')";
	
	private static final String EXPERIMENT_QUERY = "SELECT * FROM (\n" + 
			"SELECT xnat_abstractresource_id, abst.label, xme.element_name,'resources'::TEXT AS category, NULL::TEXT AS cat_id, ''::TEXT AS cat_desc \n" + 
			"FROM xnat_experimentdata_resource res_map \n" + 
			"JOIN xnat_abstractresource abst ON res_map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
			"JOIN xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id \n" +   BY_ID_WHERE   + 
			"UNION\n" + 
			"SELECT xnat_abstractresource_id, abst.label, xme.element_name, 'scans'::TEXT, isd.id, isd.type \n" + 
			"FROM xnat_imagescanData isd \n" + 
			"JOIN  xnat_abstractresource abst  ON isd.xnat_imagescandata_id = abst.xnat_imagescandata_xnat_imagescandata_id \n" +  BY_ID_WHERE  + 
			"JOIN xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id\n "
			+ ") all_resources";

	private static final RowMapper<XnatExperimentResource> ROW_MAPPER = new RowMapper<XnatExperimentResource>() {
		@Override
		public XnatExperimentResource mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatExperimentResource(resultSet);
		}
	};

	private final NamedParameterJdbcTemplate _template;

}
