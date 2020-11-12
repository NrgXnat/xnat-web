package org.nrg.xnat.services.scans.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatImagescandata;
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
	public List<XnatImagescandata> findScanTypesByProject(UserI user, String projectId) {
		return _template.query(SCAN_QUERY + BY_PROJECT_ID_WHERE + GROUP_BY, new MapSqlParameterSource("projectId", projectId), new ImageScanRowMapper(user));
	}

	@Override
	public List<XnatImagescandata> getAllScanTypes(UserI user) {
		return XnatImagescandata.getAllXnatImagescandatas(user, false);
	}
	
	@Override
	public List<XnatImagescandata> findByAssessed(UserI user, String assessedId) {
		return _template.query(ASSESSED_SCAN_QUERY, new MapSqlParameterSource("assessedId", assessedId), new ImageScanRowMapper(user));
	}

	@Override
	public XnatImagescandata findByAssessedAndScan(UserI user, String assessedId, String scanId) {
		return _template.queryForObject(ASSESSED_AND_SCAN_QUERY, new MapSqlParameterSource("assessedId", assessedId).addValue("scanId", scanId), new ImageScanRowMapper(user));
	}
	
	private static class ImageScanRowMapper implements RowMapper<XnatImagescandata> {

		ImageScanRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XnatImagescandata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			Integer scanId = resultSet.getInt("xnat_imagescandata_id");
			XnatImagescandata xnatImagescandata = XnatImagescandata.getXnatImagescandatasByXnatImagescandataId(scanId, _user, false);
			return xnatImagescandata;
		}
		private final UserI _user;
	}
	
	
	private static final String BY_PROJECT_ID_WHERE =" WHERE session.project= :projectId ";
	
	private static final String GROUP_BY = " GROUP BY scan.type, scan.xnat_imagescandata_id  ORDER BY scan.type";
	
	
	private static final String BY_ASSESSED_ID_WHERE =" SECURITY WHERE (( (xnat_imageScanData0= :assessedId)) AND ( (xnat_imageScanData0= :assessedId)))) ";
	
	private static final String BY_SCAN_ID_WHERE ="  WHERE xnat_imageScanData.id= :scanId";

	private  final String ASSESSED_SCAN_QUERY = EXPERIMENT_SCAN_SUB_QUERY_1 + BY_ASSESSED_ID_WHERE +  EXPERIMENT_SCAN_SUB_QUERY_2 ;
	
	
	private  final String ASSESSED_AND_SCAN_QUERY = EXPERIMENT_SCAN_SUB_QUERY_1 + BY_ASSESSED_ID_WHERE +  EXPERIMENT_SCAN_SUB_QUERY_2 + BY_SCAN_ID_WHERE ;

	private static final String SCAN_QUERY = "SELECT  scan.series_description, scan.type, scan.xnat_imagescandata_id \n" + 
											 "FROM xnat_imagescandata scan\n" + 
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
