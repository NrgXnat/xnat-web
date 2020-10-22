/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.model.subjects.XnatProject;
import org.nrg.xapi.model.subjects.XnatScan;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.ScanListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author afour
 *
 */
@Service
@Slf4j
public class ScanListServiceImpl implements ScanListService {
	
	@Autowired
	public ScanListServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}
	@Override
	public XnatScan create(UserI user, XnatScan item) {
		return null;
	}

	@Override
	public List<XnatScan> getAll(UserI user) {
		return null;
	}

	@Override
	public XnatScan get(UserI user, int itemId) {
		return null;
	}

	@Override
	public XnatScan findById(UserI user, String itemId) {
		return null;
	}

	@Override
	public List<XnatScan> findScansByExperimentId(UserI user, String experimentId) {
		return _template.query(SCAN_QUERY + BY_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId), ROW_MAPPER);
	}
	
	
	private static final String BY_ID_WHERE = " WHERE xnat_imageScanData.image_session_id = :experimentId";

	private static final String SCAN_QUERY = "  SELECT xnat_imageScanData.xnat_imagescandata_id , \n" + 
			"xnat_imageScanData.id AS id, xnat_imageScanData.type AS type, \n" + 
			"xnat_imageScanData.quality AS quality, table1.element_name AS xsiType, \n" + 
			"xnat_imageScanData.note AS note, \n" + 
			"xnat_imageScanData.series_description AS series_description \n" + 
			"FROM xnat_imageScanData \n" + 
			"LEFT JOIN xdat_meta_element table1 ON xnat_imageScanData.extension=table1.xdat_meta_element_id  ";

	private static final RowMapper<XnatScan> ROW_MAPPER = new RowMapper<XnatScan>() {
		@Override
		public XnatScan mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatScan(resultSet);
		}
	};

	private final NamedParameterJdbcTemplate _template;

}
