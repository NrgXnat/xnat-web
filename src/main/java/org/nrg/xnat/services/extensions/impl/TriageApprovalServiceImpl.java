package org.nrg.xnat.services.extensions.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Features;
import org.nrg.xft.exception.InvalidItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.helpers.uri.URIManager.DataURIA;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.extensions.TriageApprovalSerivce;
import org.nrg.xnat.services.triage.TriageService;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TriageApprovalServiceImpl implements TriageApprovalSerivce {

	
	private static final String OVERWRITE = "overwrite";
	private static final String SRC = "src";
	private static final String DEST = "dest";
	Map<URIManager.DataURIA,ResourceURII> moves=Maps.newHashMap();
	Boolean overwrite=Boolean.FALSE;
		
	String src=null,dest=null;
	
	Date eventTime=Calendar.getInstance().getTime();
	
	ListMultimap<String,Object> otherParams=ArrayListMultimap.create();
	
	private TriageService triageService;
	@Override
	public void create(UserI user, String src, String dest, String overwr, String eventId) throws InitializationException, ActionException {
		try {
			//final Representation entity = this.getRequest().getEntity();
//			if (RequestUtil.isMultiPartFormData(entity)) {
//				loadParams(new Form(entity));
//			}
//			
//			loadQueryVariables();
			
			moves = getMoveData(user);
			if(moves.size()==0){
				throw new DataFormatException("Missing src and dest path");
			}
			successStatus(user,eventId);
		}catch (Exception e) {
			log.error("",e);
			throw new InitializationException(e.getMessage());
		}
	}
	
	private void successStatus(UserI user, String eventId) throws Exception {
		List<String> duplicates=new ArrayList<String>();
		for(Entry<URIManager.DataURIA, ResourceURII> entry: moves.entrySet()){
			boolean locked= triageService.isLocked(user, XnatEventUtil.getEventId(eventId), overwrite, otherParams, entry.getKey(), entry.getValue());
			if(!locked){
				duplicates.addAll(triageService.move(user,XnatEventUtil.getEventId(eventId),overwrite,otherParams,entry.getKey(),entry.getValue()));
				if(!overwrite && duplicates.size()>0){
					success(HttpStatus.CREATED  ,"Duplicate File(s) found.");
				}else{
					success(HttpStatus.CREATED,"Quarantine File(s) successfully approved");
				}
			}else{
				success(HttpStatus.OK,"Destination is locked. Please unlock to continue.");
			}
		}
		
	}

	private Map<DataURIA, ResourceURII> getMoveData(UserI user) throws DataFormatException, InsufficientPrivilegesException, ClientException {
		if(StringUtils.isNotEmpty(src)){
			if(StringUtils.isEmpty(dest)){
				throw new DataFormatException( "Missing dest path");
			}
			DataURIA uriSource=convertKey(src);
			ResourceURII uriDestination=convertValue(dest);
			
			try {
				if(!canEditSource(uriSource, user)){
					throw new InsufficientPrivilegesException( "Unauthorized");
				}
				if(!canEditDestination(uriDestination, user)){
					throw new InsufficientPrivilegesException( "Unauthorized");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			moves.put(uriSource, uriDestination);
		}
		return moves;
	}

	public void success(HttpStatus status, String msg){
		//this.getResponse().setStatus(status,msg);
		
	//	String _return =this.retrieveParam(_ON_SUCCESS_RETURN_JS);
//		if(_return !=null){
//			getResponse().setEntity(new StringRepresentation("<script>"+_return + "</script>", MediaType.TEXT_HTML));
//		}else{
//			_return =this.retrieveParam(_ON_SUCCESS_RETURN_HTML);
//			if(_return !=null){
//				getResponse().setEntity(new StringRepresentation(msg , MediaType.TEXT_HTML));
//			}else{
//				getResponse().setEntity(new StringRepresentation(msg , MediaType.TEXT_HTML));
//			}
//		}
	}
	
//	private TriageService getTriageService() {
//        if (triageService == null) {
//            triageService = XDAT.getContextService().getBean(TriageService.class);
//        }
//        return triageService;
//    }
	
	private boolean canEditSource(final DataURIA uriSource, UserI user) throws InvalidItemException, Exception{
		boolean authorized=false;
		try{
			
			String sproject=(String)uriSource.getProps().get(URIManager.PROJECT_ID);
			XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(sproject,user, false);
			if(proj!=null && Features.checkFeature(user,proj.getSecurityTags().getHash().values(), "QuarantineReview")){
				authorized=true;
			}else{
				authorized=false;
			}
		}catch(Exception e){
			authorized=false;
		}
		return authorized;
	}
	
	private boolean canEditDestination(final ResourceURII arcURI, UserI user) throws InvalidItemException, Exception{
		ArchivableItem item = arcURI.getSecurityItem();
		if (item == null) {
			throw new Exception("URI " + arcURI.getUri() + " is not, or does not belong to, an existing XNAT item " +
					"(said item may have been deleted)");
		}
		return item.canEdit(user);
	}
	
	
	public void handleParam(final String key,final Object value) throws ClientException{
		if(value!=null){
			if(key.contains("/")){
				moves.put(convertKey(key), convertValue((String)value));
			}else if(key.equals(SRC)){
				src=(String)value;
			}else if(key.equals(DEST)){
				dest=(String)value;
			}else if(key.equals(OVERWRITE)){
				overwrite=Boolean.valueOf((String)value);
			}else{
				otherParams.put(key, value);
			}
		}
	}
	
	public URIManager.DataURIA convertKey(final String key) throws ClientException{
		try {
			URIManager.DataURIA uri=UriParserUtils.parseURI(key);
			
			if(uri instanceof URIManager.TriageURI){
				return (URIManager.TriageURI)uri;
			}else{
				throw new ClientException("Invalid Source:"+ key);
			}
		} catch (MalformedURLException e) {
			throw new ClientException("Invalid Source:"+ key,e);
		}
	}
	
	public ResourceURII convertValue(final String key) throws ClientException{
		try {
			URIManager.DataURIA uri=UriParserUtils.parseURI(key);
			
			if(uri instanceof ResourceURII){
				return (ResourceURII)uri;
			}else{
				throw new ClientException("Invalid Destination:"+ key);
			}
		} catch (MalformedURLException e) {
			throw new ClientException("Invalid Destination:"+ key,e);
		}
	}

}
