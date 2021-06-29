package org.nrg.xnat.services.extensions.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Features;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.exception.InvalidItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.extensions.util.TriageUtil;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.extensions.TriageService;
import org.nrg.xnat.services.triage.TriageManifest;
import org.nrg.xnat.services.triage.TriageUtils;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TriageServiceImpl implements TriageService{

	@Override
	public List<TriageUtil> findTriageByProjectId(UserI user, String projectId, HttpServletRequest request) {
		String projectPath=TriageUtils.getTriageProjectPath(projectId);
		XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		return returnXnameList(proj,projectPath+File.separator+"resources", request);
	}
	
	private List<TriageUtil> returnXnameList(XnatProjectdata proj, String projectPath, HttpServletRequest request) {
		List<TriageUtil> response = new ArrayList<>();
		File[] fileArray = new File(projectPath).listFiles();
		 if(fileArray!=null){
			 for (File f : fileArray) {
				 String fn=f.getName();
				 response.add(getTriageUtil(fn, f, request));
			 }
		 }
		 //sendTableRepresentation : Pending Impl
		return response;
	}
	
	@Override
	public void findTriageByProjectIdAndXname(UserI user, String projectId, String xName, HttpServletRequest request) {
		
	}

	@Override
	public void findTriagefilesByProjectIdAndXname(UserI user, String projectId, String xName, HttpServletRequest request) {
		
	}

	@Override
	public void findTriagefilesByProjectIdAndXnameAndFiles(UserI user, String projectId, String xName, String file, HttpServletRequest request) {
		
	}

	@Override
	public void deleteTriage(UserI user, String projectId, String xname, String file, String eventReason, String eventComment, String eventId) {
		try {   
		XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
			String projectPath=TriageUtils.getTriageProjectPath(projectId);
	        if(proj!=null && proj.canRead(user)){//only continue when the user can read the project.
	        	
		        if (xname == null && file == null) {
		        	//fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation.");
		        } else if (xname != null && file == null) {
		        	deleteTriageResource(proj,projectPath,xname,user,eventReason, eventComment, eventId, projectId);
		        } else if (xname != null && file != null) {
		        	deleteTriageFiles(proj,projectPath,xname,file, user, eventReason, eventComment,eventId,projectId);
		        }
	        }
		} catch (Exception e) {
			//fail(Status.SERVER_ERROR_INTERNAL,e.getMessage());
			log.error("",e);
		}
	}


	private void deleteTriageFiles(XnatProjectdata proj, String projectPath, String xname, String file, UserI user, String eventReason, String eventComment, String eventId, String projectId) throws Exception {
		File fi = new File (projectPath+File.separator+ File.separator+"resources"+File.separator+xname+File.separator+"files"+File.separator+file);
		if(canDelete(proj, fi,user)){
			ArrayList<File> fileList=new ArrayList<File>();
	
	        if (fi.exists()) {
	           	fileList.add(fi);
	        }
			
			boolean deleteOK = true;
			if (fileList.size()>0) {
	            for (File f : fileList) {  
	            	if (f.isDirectory()) {
	            		try {
	            			FileUtils.deleteDirectory(f);
	            		} catch (IOException e) {
	            			deleteOK = false;
	            		}
	            	} else {
	            		if (!f.delete()) {
	            			deleteOK = false;
	            		}
	            	}
	            }
	            if (deleteOK) {
		        	workflow(true,"Delete Quarantine Files", eventReason, eventComment,user, eventId, projectId);
	            } else {
	            	throw new InitializationException("Problem deleting one or more server files.");
	            }
			} else {
				throw new NotFoundException("No matching files found.");
			}
		}else{
			throw new InsufficientPrivilegesException("User account doesn't have permission to delete this file.");
		}
	}
	
	

	private void deleteTriageResource(XnatProjectdata proj, String projectPath, String xname, UserI user, String eventReason, String eventComment, String eventId, String projectId) throws Exception {
		File dir = new File (projectPath+File.separator+RESOURCES+File.separator+xname);
		if(this.canDelete(proj, dir, user)){
			if (dir.exists() && dir.isDirectory()) {
				
				try {
					FileUtils.deleteDirectory(dir);
		        	workflow(true,"Delete Quarantine Files",eventReason, eventComment, user, eventId, projectId);

				} catch (IOException e) {
					log.error("",e);
					throw new InitializationException(e.getMessage());
				}
			} else {
				throw new NotFoundException("Quarantine directory not found or is not a directory.");
			}
		}else{
			throw new InsufficientPrivilegesException("User account doesn't have permission to modify this resource.");
		}
	}
	
	public void workflow(boolean status,String action,String reason,String comment, UserI user, String eventId, String projectId) throws Exception{
		PersistentWorkflowI work=WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(eventId), user, "xnat:projectData", projectId, projectId ,EventUtils.newEventInstance(EventUtils.CATEGORY.DATA,EventUtils.TYPE.WEB_FORM, action,reason,comment));
		if(status){
			WorkflowUtils.complete(work, work.buildEvent());		
		}else{
			WorkflowUtils.fail(work, work.buildEvent());
		}
	}
	
	boolean canDelete(XnatProjectdata proj,File file, UserI user) throws InvalidItemException, Exception{
        boolean allowed=false;
        if (Features.checkFeature(user,proj.getSecurityTags().getHash().values(), "QuarantineReview") || StringUtils.equals(user.getUsername(),getUser(file))){
            allowed= true;
        }else{
        	allowed= false;
        }
		return allowed;
	}
	
	String getUser(File file){
		String username="";
		File manifest=new File(file+File.separator+FILES+File.separator+MANIFEST);
		if(file.isFile()){
			username=this.getPropertyFromManifest(file, USER);
		}else{
			username=this.getPropertyFromManifest(manifest, USER);
		}
		return username;
	}

	private TriageUtil getTriageUtil(String fn, File f, HttpServletRequest request) {
		return TriageUtil.builder()
				 .resource(fn)
				 .uri(constructResourceURI(fn, request))
				 .target(getPropertyFromManifest(f, TARGET))
				 .user(getPropertyFromManifest(f, USER))
				 .date(getPropertyFromManifest(f, DATE))
				 .overwrite(getPropertyFromManifest(f, OVERWRITE))
				 .eventReason(getPropertyFromManifest(f, EVENT_REASON))
				 .ftarget(getPropertyFromManifest(f, FTARGET))
				 .format(getPropertyFromManifest(f, FORMAT))
				 .content(getPropertyFromManifest(f, CONTENT))
				 .fSource("").build();
	}

	private String constructResourceURI(String resource, HttpServletRequest request) {
		String requestPart = this.mapToProjectResources(request.getServletPath() + request.getPathInfo());

		if (!requestPart.endsWith(resource)) {
			requestPart += File.separator + resource;
		}
		return requestPart;

	}

	String mapToProjectResources(String originalResourceUrl) {
		String mapped = originalResourceUrl;
		int bindex = StringUtils.indexOf(originalResourceUrl, "/subjects/");
		// only compress urls for subject and deeper
		if (bindex > 0) {
			int lindex = StringUtils.lastIndexOf(originalResourceUrl, "/resources");
			String extra = originalResourceUrl.substring(bindex, lindex);
			mapped = originalResourceUrl.replace(extra, "");
		}
		return originalResourceUrl;
	}

	private String getPropertyFromManifest(File resourceFile, String prop) {
		ObjectMapper mapper = new ObjectMapper();
		String target = "";
		try {
			File manifestFile = getManifestFileFromResource(resourceFile);
			TriageManifest tManifest = mapper.readValue(manifestFile, TriageManifest.class);
			target = tManifest.getFirstMatchingEntry(prop);
		} catch (JsonParseException e) {
			log.warn(e.getMessage());
		} catch (JsonMappingException e) {
			log.warn(e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
		return target;
	}

	private File getManifestFileFromResource(File resourceFile) {
		File manifestFile = new File(resourceFile.getParentFile().getAbsolutePath() + File.separator + MANIFEST);
		if (!manifestFile.exists()) {
			manifestFile = new File(resourceFile.getAbsolutePath() + File.separator + FILES + File.separator + MANIFEST);
		}
		return manifestFile;
	}

	private final String RESOURCES ="resources";
	private static final String FORMAT = "format";
	private static final String CONTENT = "content";
	private final String FILES ="files";
	private final String MANIFEST=".manifest";	
	private final String TARGET = "TARGET";
	private final String FTARGET = "FTARGET";
	private final String USER = "USER";
	private final String DATE = "DATE";
	private final String EVENT_REASON = "EVENT_REASON";
	private final String OVERWRITE = "OVERWRITE";
	private static final String _ON_FAILURE_RETURN_JS = "_onFailureReturnJS";
	private static final String _ON_FAILURE_RETURN_HTML = "_onFailureReturnHTML";
	
}
