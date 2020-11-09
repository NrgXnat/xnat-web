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
		return _template.query(SCAN_QUERY + BY_SCAN_ID_WHERE + GROUP_BY, new MapSqlParameterSource("projectId", projectId), new ScanRowMapper(user));
	}

	@Override
	public List<XnatScscandata> getAllScanTypes(UserI user) {
		return _template.query(SCAN_QUERY  + GROUP_BY, new ScanRowMapper(user));
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
	
	private static final String BY_SCAN_ID_WHERE =" WHERE session.project= :projectId ";
	
	private static final String GROUP_BY = " GROUP BY scan.type, scan.xnat_imagescandata_id  ORDER BY scan.type";
	
	private static final String SCAN_QUERY = "SELECT xs_a_concat(series_description || ',') AS series_descriptions, scan.type, scan.xnat_imagescandata_id  \n" + 
											 "FROM xnat_mrScanData mr \n" + 
											 "LEFT JOIN xnat_imageScanData scan ON mr.xnat_imageScanData_id=scan.xnat_imageScanData_id \n" + 
											 "LEFT JOIN xnat_experimentData session ON scan.image_session_id=session.id ";
	
	
	private final NamedParameterJdbcTemplate _template;

}
