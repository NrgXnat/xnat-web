package org.nrg.xnat.services.scans.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatScscandata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.scans.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ScanServiceImpl implements ScanService {

	@Autowired
	public ScanServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}
	
	@Override
	public List<XnatScscandata> findScanTypesByProject(UserI user, String projectId) {
		return _template.query(SCAN_QUERY + BY_SCAN_ID_WHERE + GROUP_BY, new MapSqlParameterSource("projectId", projectId), new ScanRowMapper1(user));
	}

	@Override
	public List<XnatScscandata> getAllScanTypes(UserI user) {
		return _template.query(SCAN_QUERY  + GROUP_BY, new ScanRowMapper(user));
	}
	
	@Override
	public List<XnatScscandata> findByExperiments(UserI user, String experimentId) {
		return _template.query(EXPERIMENT_SCAN_SUB_QUERY, new MapSqlParameterSource("experimentId", experimentId), new ScanRowMapper1(user));
	}


	
	private static class ScanRowMapper implements RowMapper<XnatScscandata> {

		ScanRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XnatScscandata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			XnatScscandata xnatScscandata = new XnatScscandata();
			//Integer scanId = resultSet.getInt("xnat_imagescandata_id");
			//XnatScscandata xnatScscandata = XnatScscandata.getXnatScscandatasByXnatImagescandataId(scanId, _user,false);
			xnatScscandata.setSeriesDescription(resultSet.getString("series_descriptions"));
			xnatScscandata.setType(resultSet.getString("type"));
			xnatScscandata.setXnatImagescandataId(resultSet.getInt("xnat_imagescandata_id"));
			return xnatScscandata;
		}

		private final UserI _user;
	}
	
	private static class ScanRowMapper1 implements RowMapper<XnatScscandata> {

		ScanRowMapper1(final UserI user) {
			_user = user;
		}

		@Override
		public XnatScscandata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			XnatScscandata xnatScscandata = new XnatScscandata();
			//Integer scanId = resultSet.getInt("xnat_imagescandata_id");
			//XnatScscandata xnatScscandata = XnatScscandata.getXnatScscandatasByXnatImagescandataId(scanId, _user,false);
			xnatScscandata.setXnatImagescandataId(resultSet.getInt("xnat_imagescandata_id"));
			xnatScscandata.setId(resultSet.getString("id"));
			xnatScscandata.setType(resultSet.getString("type"));
			xnatScscandata.setQuality(resultSet.getString("quality"));
			xnatScscandata.setSeriesDescription(resultSet.getString("series_description"));
			xnatScscandata.setNote(resultSet.getString("note"));
			return xnatScscandata;
		}

		private final UserI _user;
	}
	
	private static final String BY_SCAN_ID_WHERE =" WHERE session.project= :projectId ";
	
	private static final String GROUP_BY = " GROUP BY scan.type, scan.xnat_imagescandata_id  ORDER BY scan.type";
	
	
	private static final String BY_EXPERIMENT_ID_WHERE =" SECURITY WHERE (( (xnat_imageScanData0= :experimentId)) AND ( (xnat_imageScanData0= :experimentId)))) ";

	private  final String EXPERIMENT_SCAN_SUB_QUERY = EXPERIMENT_SCAN_SUB_QUERY_1 + BY_EXPERIMENT_ID_WHERE +  EXPERIMENT_SCAN_SUB_QUERY_2 ;

	private static final String SCAN_QUERY = "SELECT xs_a_concat(series_description || ',') AS series_descriptions, scan.type, scan.xnat_imagescandata_id  \n" + 
											 "FROM xnat_mrScanData mr \n" + 
											 "LEFT JOIN xnat_imageScanData scan ON mr.xnat_imageScanData_id=scan.xnat_imageScanData_id \n" + 
											 "LEFT JOIN xnat_experimentData session ON scan.image_session_id=session.id ";
	
	private static final String EXPERIMENT_SCAN_SUB_QUERY_1	= "SELECT xnat_imageScanData.xnat_imagescandata_id , xnat_imageScanData.id , xnat_imageScanData.type , \n" + 
			   " xnat_imageScanData.quality , table1.element_name ,  xnat_imageScanData.note , xnat_imageScanData.series_description  \n" + 
			   " FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (xnat_imageScanData26) * \n" + 
			   " FROM (SELECT xnat_imageScanData.xnat_imagescandata_id AS xnat_imageScanData26, \n" + 
			   " xnat_imageScanData.image_session_id AS xnat_imageScanData0 FROM xnat_imageScanData xnat_imageScanData) ";
			   
   private static final String EXPERIMENT_SCAN_SUB_QUERY_2 = "  SECURITY LEFT JOIN xnat_imageScanData SEARCH ON SECURITY.xnat_imageScanData26=SEARCH.xnat_imagescandata_id) xnat_imageScanData  \n" + 
			   " LEFT JOIN xdat_meta_element table1 ON xnat_imageScanData.extension=table1.xdat_meta_element_id";
	
	
	private final NamedParameterJdbcTemplate _template;

}
