package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExperimentServiceImpl implements ExperimentService {

	@Autowired
	public ExperimentServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}
	@Override
	public XnatExperimentdata create(UserI user, XnatExperimentdata xnatExperimentdata) {
		return null;
	}

	@Override
	public List<XnatExperimentdata> getAll(UserI user) {
		return XnatExperimentdata.getAllXnatExperimentdatas(user, false);
	}

	@Override
	public List<XnatExperimentdata> getAllExperiments(UserI user) {
		System.out.println("########## getAllExperiments ###########");
		return _template.query(EXPERIMENT_QUERY, new ExperimentRowMapper(user));
	}

	@Override
	public XnatExperimentdata findById(UserI user, String experimentId) {
		return XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
	}

	@Override
	public XnatExperimentdata findByExperimentId(UserI user, String experimentId) {
		System.out.println("########## findByExperimentId ###########"+ experimentId );
		return _template.queryForObject(EXPERIMENT_QUERY + BY_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId),new ExperimentRowMapper(user));
	}

	@Override
	public List<XnatExperimentdata> findByProject(UserI user, String projectId) {
		String xmlPath = "";
		return XnatExperimentdata.getXnatExperimentdatasByField(xmlPath, projectId, user, false);
	}

	@Override
	public XnatExperimentdata update(UserI user, XnatExperimentdata xnatExperimentdata, String experimentId) {
		return null;
	}

	@Override
	public void deleteById(UserI user, String experimentId) {
	}

	@Override
	public List<XnatExperimentdata> findByLabel(UserI user, String label) {
		return null;
	}

	private static final String BY_ID_WHERE = " WHERE xnat_experimentData.id = :experimentId";

	private static final String EXPERIMENT_QUERY = "SELECT xnat_experimentData.id AS id, xnat_experimentData.project AS project, \n" +
			"xnat_experimentData.date AS date, xdat_meta_ele.element_name AS xsiType, \n" +
			"xnat_experimentData.label AS label, \n" +
			"xnat_exp_meta_data.insert_date AS insertDate FROM xnat_experimentData xnat_experimentData   \n" +
			"LEFT JOIN xdat_meta_element xdat_meta_ele ON xnat_experimentData.extension=xdat_meta_ele.xdat_meta_element_id   \n" +
			"LEFT JOIN xnat_experimentData_meta_data xnat_exp_meta_data ON xnat_experimentData.experimentData_info=xnat_exp_meta_data.meta_data_id";



//	 public static final RowMapper<XnatExperimentdata> ROW_MAPPER = new RowMapper<XnatExperimentdata>() {
//	        @Override
//	        public XnatExperimentdata mapRow(final ResultSet resultSet, final int index) throws SQLException {
//	        	XnatExperimentdata xnatExperimentdata = new XnatExperimentdata();
//	        	xnatExperimentdata.setId(resultSet.getString(1));
//	        	xnatExperimentdata.setProject(resultSet.getString(2));
//	        	xnatExperimentdata.setDate(resultSet.getDate(3));
//	        	xnatExperimentdata.setLabel(resultSet.getString(5));
//				return xnatExperimentdata;
//
//	        }
//	 };

	private static class ExperimentRowMapper implements RowMapper<XnatExperimentdata> {
	    ExperimentRowMapper(final UserI user) {
	        _user = user;
	    }

	    @Override
	    public XnatExperimentdata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	    	System.out.println("#######XnatExperimentdata MapRow : id########" + resultSet.getString("id"));
	    	System.out.println("#######XnatExperimentdata MapRow : project########" + resultSet.getString("project"));
	    	System.out.println("#######XnatExperimentdata MapRow : label########" + resultSet.getString("label"));
	    	System.out.println("#######XnatExperimentdata MapRow : date########" + resultSet.getString("date"));
	        final String experimentId = resultSet.getString("id");
	        XnatExperimentdata xnatExperimentdata= XnatExperimentdata.getXnatExperimentdatasById(experimentId, _user, false);
	        System.out.println("#######XnatExperimentdata xnatExperimentdata Result : id########"+ xnatExperimentdata);
	        return xnatExperimentdata;
	    }
	    private final UserI _user;
	}
//	 public static final RowMapper<XnatExperimentdata> ROW_MAPPER = new RowMapper<XnatExperimentdata>() {
//	        @Override
//	        public XnatExperimentdata mapRow(final ResultSet resultSet, final int index) throws SQLException {
//	        	XnatExperimentdata xnatExperimentdata = new XnatExperimentdata();
//	        	//XnatExperimentdata.getXnatExperimentdatasById(resultSet.getString("id"), user, false);
//	        	xnatExperimentdata.setId(resultSet.getString("id"));
//	        	xnatExperimentdata.setProject(resultSet.getString("project"));
//	        	xnatExperimentdata.setDate(resultSet.getDate("date"));
//	        	xnatExperimentdata.setLabel(resultSet.getString("label"));
//				return xnatExperimentdata;
//
//	        }
//	 };
//

//	 public static final RowMapper<XnatExperimentdata> ROW_MAPPER = (resultSet, index) ->{
//	 return new XnatExperimentdata(resultSet.getString("id"));
// };
	private final NamedParameterJdbcTemplate _template;

}
