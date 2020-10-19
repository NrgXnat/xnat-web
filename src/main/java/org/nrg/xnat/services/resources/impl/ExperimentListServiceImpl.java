package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.model.subjects.XnatExperiment;
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
		return _template.queryForObject(EXPERIMENT_QUERY + BY_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId),
				ROW_MAPPER);
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

	private final NamedParameterJdbcTemplate _template;

}
