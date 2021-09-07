package org.nrg.xnat.services.scans.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.turbine.utils.AdminUtils;
import org.nrg.xft.XFTTable;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.SecureResourceUtil;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.scans.ScanService;
import org.nrg.xnat.turbine.utils.ScanQualityUtils;
import org.nrg.xnat.turbine.utils.XNATUtils;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScanServiceImpl implements ScanService {

	protected XnatProjectdata proj = null;
	XnatSubjectdata sub = null;
	
	protected XnatImagesessiondata session = null;
	@Autowired
	public ScanServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}
	
	@Override
	public List<XnatImagescandata> findAllScanTypesByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		List<XnatImagescandata> scans= _template.query(SCAN_QUERY + BY_PROJECT_ID_WHERE + GROUP_BY, new MapSqlParameterSource("projectId", projectId), new ImageScanRowMapper(user));
		if(Objects.isNull(scans) || scans.isEmpty()) {
    		throw new  NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME) ;
		}
    	return scans;
	}

	@Override
	public List<XnatImagescandata> findAllScanTypes(UserI user) throws NotFoundException {
		List<XnatImagescandata> scans= XnatImagescandata.getAllXnatImagescandatas(user, false);
		if(Objects.isNull(scans) || scans.isEmpty()) {
    		throw new  NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME) ;
		}
    	return scans;
	}
	
	@Override
	public List<XnatImagescandata> findAllByAssessedId(UserI user, String assessedId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(assessedId)) {
    		throw new DataFormatException("The requested assessed ID" + assessedId + " wasn't found ");
		}
		List<XnatImagescandata> scans= _template.query(ASSESSED_SCAN_QUERY, new MapSqlParameterSource("assessedId", assessedId), new ImageScanRowMapper(user));
		if(Objects.isNull(scans) || scans.isEmpty()) {
    		throw new  NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME) ;
		}
    	return scans;
	}

	@Override
	public Optional<XnatImagescandata> findByAssessedIdAndScanId(UserI user, String assessedId, Integer scanId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(assessedId)) {
    		throw new DataFormatException("The requested assessed ID" + assessedId + " wasn't found ");
		}
		if(Objects.isNull(scanId)) {
    		throw new DataFormatException("The requested scan ID" + scanId + " wasn't found ");
		}
		XnatImagescandata scan = _template.queryForObject(ASSESSED_AND_SCAN_QUERY, new MapSqlParameterSource("assessedId", assessedId).addValue("scanId", scanId), new ImageScanRowMapper(user));
		if(Objects.isNull(scan)) {
    		throw new  NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME) ;
		}
    	return Optional.of(scan);
	}
	
	@Override
	public List<XnatImagescandata> findAllByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId,String experimentId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID" + subjectId + " wasn't found ");
		}
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID" + experimentId + " wasn't found ");
		}
		List<XnatImagescandata> scans  =_template.query(PROJECT_SUBJECT_EXPERIMENT_SCAN_QUERY, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId",experimentId), new ImageScanRowMapper(user));
		if(Objects.isNull(scans) || scans.isEmpty()) {
    		throw new  NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME) ;
		}
    	return scans;
	}

	@Override
	public Optional<XnatImagescandata> findByProjectIdAndSubjectIdAndExperimentIdAndScanId(UserI user, String projectId, String subjectId, String experimentId, Integer scanId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID" + subjectId + " wasn't found ");
		}
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID" + experimentId + " wasn't found ");
		}
		if(Objects.isNull(scanId)) {
    		throw new DataFormatException("The requested scan ID" + scanId + " wasn't found ");
		}
		XnatImagescandata scan  = _template.queryForObject(PROJECT_SUBJECT_EXPERIMENT_SCAN_WITH_SCAN_ID_QUERY_, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId",experimentId).addValue("scanId", scanId), new ImageScanRowMapper(user));
		if(Objects.isNull(scan)) {
    		throw new  NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME) ;
		}
    	return Optional.of(scan);
	}
	
	
	@Override
	public List<Map<String, String>> findAllScanners(UserI user, String scanTable, String projectId) throws InsufficientPrivilegesException {
		if(Objects.nonNull(projectId)) {
			proj =  XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		}
		if(scanTable==null){
			scanTable="xnat_mrSessionData";
		}else{
			if(!(scanTable.equalsIgnoreCase("xnat_mrSessionData") || scanTable.equalsIgnoreCase("xnat_petSessionData") || scanTable.equalsIgnoreCase("xnat_ctSessionData"))){
				AdminUtils.sendAdminEmail(user,"Possible SQL Injection attempt.", "User passed "+ scanTable+" as a table name.");
				throw new InsufficientPrivilegesException(user.getUsername());
            }
		}
		return getScannersData(user, scanTable, projectId, proj);
		
	}
	
	@Override
	public String findAllScanQualityLable(UserI user, String projectId) throws InitializationException {
		if (log.isDebugEnabled()) {
			log.debug("Entering the scan quality label represent() method");
		}
		try {
			List<String> labels = ScanQualityUtils.getQualityLabels(projectId, user);
			JSONObject json = new JSONObject();
			json.put(StringUtils.isBlank(projectId) ? SITE_KEY : projectId, labels);
			return json.toString();
		} catch (JSONException e) {
			throw new InitializationException(e);
		}
	}
	
	private List<Map<String, String>> getScannersData(UserI user, String scanTable, String projectId, XnatProjectdata proj) {
		List<Map<String, String>> response = new ArrayList<>();
		try {
			List<String> results = new ArrayList<>();
			String query="SELECT DISTINCT isd.scanner FROM " + scanTable + " mod LEFT JOIN xnat_imageSessionData isd ON mod.id=isd.id LEFT JOIN xnat_experimentData expt ON isd.id=expt.id WHERE isd.scanner IS NOT NULL";
			if(proj!=null) {
				query+=" WHERE expt.project='" + proj.getId() + "'";
				results = _template.query(query , new MapSqlParameterSource(), new ScannerRowMapper());
			}else {
				results = _template.query(query, new MapSqlParameterSource(), new ScannerRowMapper());
			}
			results.forEach(res->{
				Map<String, String> result = new HashMap<>();
				result.put(SCANNER_KEY, res);
				response.add(result);
			});

		} catch (Exception e) {
			log.error("", e);
		}
		return response;
	}

	@Override
	public void deleteById(UserI user, String assessedId, Integer scanId,String filepath,boolean removeFiles,XnatEventUtil event) throws NotFoundException, DataFormatException, InitializationException {
		delete(user, findByAssessedIdAndScanId(user,assessedId, scanId ).get(), assessedId, scanId,filepath,removeFiles, event );
	}
	

	public void delete(UserI user, XnatImagescandata scan, String assessedId, Integer scanId, String filepath,boolean removeFiles, XnatEventUtil event) throws NotFoundException, DataFormatException, InitializationException {
		SecureResourceUtil secureResoureUtil = new SecureResourceUtil();
		if (assessedId != null) 
			session = (XnatImagesessiondata) XnatExperimentdata.getXnatExperimentdatasById(assessedId, user, false);
        
		 searchForScan(user,scan, scanId);
		 
		 if (scan == null)  
			 throw new NotFoundException("Unable to find the specified scan.");

			if (filepath != null && !filepath.equals("")) 
				throw new DataFormatException("Bad request");
			
	        try {
	        	boolean prevent_delete=StringUtils.contains(XDAT.getSiteConfigurationProperty("security.prevent-data-deletion-override", "[]"), session.getItem().getStatus())?false: XDAT.getBoolSiteConfigurationProperty("security.prevent-data-deletion", false);
	        	
	        	if (!Permissions.canDelete(user, session) || prevent_delete) 
	        		throw new InsufficientPrivilegesException("User account doesn't have permission to modify this session.");
	        
	        	secureResoureUtil.delete(session, scan, removeFiles,XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getDeleteAction(scan.getXSIType()), event), event,user);

	            // Above "delete" removes resources, but leaves dangling scan directory
	            XNATUtils.removeScanDir(session, scan);

	        } catch (SQLException e) {
	            log.error("There was an error running a query.", e);
	            throw new InitializationException("There was an error running a query");
	        } catch (ConfigServiceException e) {
	        	log.error("There was an error.", e);
	        	 throw new InitializationException("There was an error");
	        } catch (Exception e) {
	            log.error("There was an error.", e);
	            throw new InitializationException("There was an error");
	        }
		}

	protected boolean completeDocument = false;

	protected void searchForScan(UserI user, XnatImagescandata scan, Integer scanId) {
		if (scan == null && scanId != null) {
			if (session != null) {
				CriteriaCollection cc = new CriteriaCollection("AND");
				cc.addClause("xnat:imageScanData/ID", scanId);
				cc.addClause("xnat:imageScanData/image_session_ID", session.getId());
				ArrayList<XnatImagescandata> scans = XnatImagescandata.getXnatImagescandatasByField(cc, user,
						completeDocument);
				if (scans.size() > 0) {
					scan = scans.get(0);
				}
			}
		}
	}
	
//	 public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
//	        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, getReason(), getComment());
//	    }
//	
//	private String getComment() {
//		return null;
//	}
//
//	private String getReason() {
//		return null;
//	}
//
//	private String getAction() {
//		 return "Deleted";
//	}

//	private TYPE getEventType() {
//		return EventUtils.TYPE.WEB_FORM;
//	}

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
	
	
	private static class ScannerRowMapper implements RowMapper<String> {
		@Override
		public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			return resultSet.getString(SCANNER_KEY);
		}
	}
	
	
	
	private static final String BY_PROJECT_ID_WHERE =" WHERE session.project= :projectId ";
	
	private static final String GROUP_BY = " GROUP BY scan.type, scan.xnat_imagescandata_id  ORDER BY scan.type";
	
	
	private static final String BY_ASSESSED_ID_WHERE =" SECURITY WHERE (( (image_session_id= :assessedId)) AND ( (image_session_id= :assessedId)))) ";
	
	private static final String BY_SCAN_ID_WHERE ="  WHERE xnat_imageScanData.xnat_imagescandata_id= :scanId";

	private  final String ASSESSED_SCAN_QUERY = EXPERIMENT_SCAN_SUB_QUERY_1 + BY_ASSESSED_ID_WHERE +  EXPERIMENT_SCAN_SUB_QUERY_2 ;
	
	private  final String PROJECT_SUBJECT_EXPERIMENT_SCAN_QUERY = EXPERIMENT_SCAN_SUB_QUERY_1 + BY_PROJECT_ID_AND_ASSESSED_ID_WHERE +  EXPERIMENT_SCAN_SUB_QUERY_2 ;
	
	private  final String PROJECT_SUBJECT_EXPERIMENT_SCAN_WITH_SCAN_ID_QUERY_ = EXPERIMENT_SCAN_SUB_QUERY_1 + BY_PROJECT_ID_AND_ASSESSED_ID_AND_SCAN_ID_WHERE +  EXPERIMENT_SCAN_SUB_QUERY_2 ;
	
	
	private  final String ASSESSED_AND_SCAN_QUERY = EXPERIMENT_SCAN_SUB_QUERY_1 + BY_ASSESSED_ID_WHERE +  EXPERIMENT_SCAN_SUB_QUERY_2 + BY_SCAN_ID_WHERE ;

	private static final String SCAN_QUERY = "SELECT  scan.series_description, scan.type, scan.xnat_imagescandata_id \n" + 
											 "FROM xnat_imagescandata scan\n" + 
											 "LEFT JOIN xnat_experimentData session ON scan.image_session_id=session.id ";
	
   private static final String EXPERIMENT_SCAN_SUB_QUERY_1 = "SELECT xnat_imageScanData.xnat_imagescandata_id ,\n" + 
   															 "xnat_imageScanData.id , xnat_imageScanData.type, \n" + 
   															 "xnat_imageScanData.quality , table1.element_name , \n" + 
   															 "xnat_imageScanData.note, xnat_imageScanData.project,\n" + 
   															 "xnat_imageScanData.series_description \n" + 
   															 "FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (xnat_imagescandata_id)* FROM (SELECT xnat_imageScanData.xnat_imagescandata_id AS xnat_imagescandata_id, \n" + 
   															 "xnat_imageScanData.image_session_id AS image_session_id, xnat_imageScanData.project as project FROM xnat_imageScanData xnat_imageScanData) ";
   
   private static final String EXPERIMENT_SCAN_SUB_QUERY_2 = "  SECURITY LEFT JOIN xnat_imageScanData SEARCH ON SECURITY.xnat_imagescandata_id=SEARCH.xnat_imagescandata_id)xnat_imageScanData   \n" + 
   		"LEFT JOIN xdat_meta_element table1 ON xnat_imageScanData.extension=table1.xdat_meta_element_id";
   
   private static final String BY_PROJECT_ID_AND_ASSESSED_ID_WHERE = " SECURITY WHERE  (image_session_id= :experimentId) AND  (image_session_id= :experimentId) AND (project = :projectId) )";
	
   private static final String BY_PROJECT_ID_AND_ASSESSED_ID_AND_SCAN_ID_WHERE = " SECURITY WHERE  (image_session_id= :experimentId) AND  (image_session_id= :experimentId) AND (project = :projectId)  AND (xnat_imagescandata_id = :scanId) )";
   
   private static final String SCANNER_KEY= "scanner";
   
   private final NamedParameterJdbcTemplate _template;
   
   private static final String SITE_KEY = "site";

}
