package org.nrg.xnat.services.scans.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.om.XnatCtsessiondata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatPetsessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.Authorizer;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.model.util.SecureResoureUtil;
import org.nrg.xnat.services.scans.ScanService;
import org.nrg.xnat.turbine.utils.XNATUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Variant;
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
	public XnatImagescandata findByAssessedAndScan(UserI user, String assessedId, Integer scanId) {
		return _template.queryForObject(ASSESSED_AND_SCAN_QUERY, new MapSqlParameterSource("assessedId", assessedId).addValue("scanId", scanId), new ImageScanRowMapper(user));
	}
	
	@Override
	public List<XnatImagescandata> findByProjectAndSubjectAndExperiment(UserI user, String projectId, String subjectId,String experimentId) {
		return null;
	}

	@Override
	public XnatImagescandata findByProjectAndSubjectAndExperimentAndScan(UserI user, String projectId, String subjectId, String experimentId, String scanId) {
		return null;
	}
	
	
	@Override
	public XnatImagescandata create(UserI user, String projectId, String subjectId, String assessedId, XnatImagescandata scan) throws NotFoundException {
		SecureResoureUtil secureResoureUtil = new SecureResoureUtil();
		
		setSessionAndProj(user,projectId,subjectId, assessedId );
		
		XFTItem item = null;

		try {
			String dataType=null;
			if(session instanceof XnatMrsessiondata){
				dataType="xnat:mrScanData";
			}else if(session instanceof XnatPetsessiondata){
				dataType="xnat:petScanData";
			}else if(session instanceof XnatCtsessiondata){
				dataType="xnat:ctScanData";
			}
			item= scan.getItem();

			if(item == null){
				String xsiType=getQueryVariable("xsiType");
				if(xsiType!=null){
					item=XFTItem.NewItem(xsiType, user);
				}
			}

			if(item==null){
				//this.getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Need POST Contents");
				//return;
			}

			if(item.instanceOf("xnat:imageScanData")){
				scan = (XnatImagescandata)BaseElement.GetGeneratedItem(item);

				//MATCH SESSION
				if(session!=null){
					scan.setImageSessionId(session.getId());
				}else{
					if(scan.getImageSessionId()!=null && !scan.getImageSessionId().equals("")){
						session=(XnatImagesessiondata)XnatExperimentdata.getXnatExperimentdatasById(scan.getImageSessionId(), user, false);

						if(session==null && proj!=null){
							session=(XnatImagesessiondata)XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), scan.getImageSessionId(), user, false);
						}
						if(session!=null){
							scan.setImageSessionId(session.getId());
						}
					}
				}

				if(scan.getImageSessionId()==null){
					//this.getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED,"Specified scan must reference a valid image session.");
					//return;
				}

				if(session==null){
					//this.getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND,"Specified image session doesn't exist.");
					//return;
				}

				if(this.getQueryVariable("type")!=null){
					scan.setType(this.getQueryVariable("type"));
				}

				//FIND PRE-EXISTING
				XnatImagescandata existing=null;

				if(scan.getXnatImagescandataId()!=null){
					existing=(XnatImagescandata)XnatImagescandata.getXnatImagescandatasByXnatImagescandataId(scan.getXnatImagescandataId(), user, completeDocument);
				}

				if(scan.getId()!=null){
					CriteriaCollection cc= new CriteriaCollection("AND");
					cc.addClause("xnat:imageScanData/ID", scan.getId());
					cc.addClause("xnat:imageScanData/image_session_ID", scan.getImageSessionId());
					ArrayList<XnatImagescandata> scans=XnatImagescandata.getXnatImagescandatasByField(cc, user, completeDocument);
					if(scans.size()>0){
						existing=scans.get(0);
					}
				}

				if(existing==null){
					if(!Permissions.canEdit(user, this.session)){
						//this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN,"Specified user account has insufficient create privileges for sessions in this project.");
						//return;
					}
					//IS NEW
					if(scan.getId()==null || scan.getId().equals("")){
						String query = "SELECT count(id) AS id_count FROM xnat_imageScanData WHERE image_session_id='" + session.getId() + "' AND id='";

						String login = null;
						if (user != null){
							login= user.getUsername();
						}
						try {
							int i=1;
							Long idCOUNT= (Long)PoolDBUtils.ReturnStatisticQuery(query + i + "';", "id_count", user.getDBName(), login);
							while (idCOUNT > 0){
								i++;
								idCOUNT= (Long)PoolDBUtils.ReturnStatisticQuery(query + i + "';", "id_count", user.getDBName(), login);
							}

							scan.setId("" + i);
						} catch (Exception e) {
							log.error("",e);
						}
					}
				}else{
					//this.getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT,"Specified scan already exists.");
					//return;
					//MATCHED
				}

				boolean allowDataDeletion=false;
				if(getQueryVariable("allowDataDeletion")!=null && getQueryVariable("allowDataDeletion").equals("true")){
					allowDataDeletion=true;
				}



				final ValidationResults vr = scan.validate();

				if (vr != null && !vr.isValid())
				{
					//this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,vr.toFullString());
					//return;
				}
		           Authorizer.getInstance().authorizeSave(session.getItem(), user);

		           secureResoureUtil.create(session, scan, false, allowDataDeletion, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(scan.getXSIType(), scan==null)), user);
				
				//this.returnSuccessfulCreateFromList(scan.getId());
			}else{
				//this.getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY,"Only Scan documents can be PUT to this address.");
			}
		} catch (ActionException e) {
			//this.getResponse().setStatus(e.getStatus(),e.getMessage());
			//return;
		} catch (InvalidValueException e) {
			//this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			log.error("",e);
		} catch (Exception e) {
			//this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			log.error("",e);
		}
		return scan;
	}
	
	private void setSessionAndProj(UserI user, String projectId, String subjectId, String assessedId) throws NotFoundException {
		if (projectId != null) {
			proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);

			if (subjectId != null) {
				sub = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, user, false);

				if (sub == null) {
					sub = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
					if (sub != null && (proj != null && !sub.hasProject(proj.getId())))
						sub = null;
				}

				if (sub != null) {
					session = XnatImagesessiondata.getXnatImagesessiondatasById(assessedId, user, false);
					if (session != null && (proj != null && !session.hasProject(proj.getId())))
						session = null;

					if (session == null)
						session = (XnatImagesessiondata) XnatImagesessiondata.GetExptByProjectIdentifier(proj.getId(),
								assessedId, user, false);
				} else {
					throw new NotFoundException("subject not found");
				}
			} else {
				throw new NotFoundException("SubjectId not found");
			}
		} else {
			session = XnatImagesessiondata.getXnatImagesessiondatasById(assessedId, user, false);

			if (session == null) {
				session = (XnatImagesessiondata) XnatImagesessiondata.GetExptByProjectIdentifier(proj.getId(), assessedId, user, false);
			}
		}
	}

	private String getQueryVariable(String string) {
		return null;
	}

	@Override
	public void deleteById(UserI user, String assessedId, Integer scanId) throws NotFoundException, DataFormatException, InitializationException {
		delete(user, findByAssessedAndScan(user,assessedId, scanId ), assessedId, scanId );
	}
	
	@Override
	public void delete(UserI user, XnatImagescandata scan, String assessedId, Integer scanId) throws NotFoundException, DataFormatException, InitializationException {
		SecureResoureUtil secureResoureUtil = new SecureResoureUtil();
		if (assessedId != null) 
			session = (XnatImagesessiondata) XnatExperimentdata.getXnatExperimentdatasById(assessedId, user, false);
        
		 searchForScan(user,scan, scanId);
		 
		 if (scan == null)  
			 throw new NotFoundException("Unable to find the specified scan.");

	        String filepath = "";
			if (filepath != null && !filepath.equals("")) 
				throw new DataFormatException("Bad request");
			
	        try {
	        	boolean prevent_delete=StringUtils.contains(XDAT.getSiteConfigurationProperty("security.prevent-data-deletion-override", "[]"), session.getItem().getStatus())?false: XDAT.getBoolSiteConfigurationProperty("security.prevent-data-deletion", false);
	        	
	        	if (!Permissions.canDelete(user, session) || prevent_delete) 
	        		throw new InsufficientPrivilegesException("User account doesn't have permission to modify this session.");
	        
	        	secureResoureUtil.delete(session, scan, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getDeleteAction(scan.getXSIType())), user);

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
	
	 public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
	        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, getReason(), getComment());
	    }
	
	private String getComment() {
		return null;
	}

	private String getReason() {
		return null;
	}

	private String getAction() {
		 return "Deleted";
	}

	private TYPE getEventType() {
		return EventUtils.TYPE.WEB_FORM;
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
	
	private static final String BY_SCAN_ID_WHERE ="  WHERE xnat_imageScanData.xnat_imagescandata_id= :scanId";

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
