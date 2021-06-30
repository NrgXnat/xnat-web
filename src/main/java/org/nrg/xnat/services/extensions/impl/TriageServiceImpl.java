package org.nrg.xnat.services.extensions.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Features;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.exception.InvalidItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.zip.ZipUtils;
import org.nrg.xnat.dto.resource.MediaTypeUtil;
import org.nrg.xnat.dto.resource.ZipRepresentationUtil;
import org.nrg.xnat.extensions.util.TriageFileUtil;
import org.nrg.xnat.extensions.util.TriageUtil;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.extensions.TriageService;
import org.nrg.xnat.services.triage.TriageManifest;
import org.nrg.xnat.services.triage.TriageUtils;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

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
		 //sendTableRepresentation : // Pending IMPL
		return response;
	}
	
	@Override
	public void findTriagefilesByProjectIdAndXname(UserI user, String projectId, String xName, HttpServletRequest request, String compression) throws Exception {
		String projectPath=TriageUtils.getTriageProjectPath(projectId);
		MediaType mt = MediaType.parseMediaType(request.getContentType());
		XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId,user, false);
    	if(proj!=null && proj.canRead(user)){
    		if(xName != null) {
            	if (isZIPRequest(mt)) {
            		returnZippedFiles(proj,projectPath,xName, user, request,compression);
            	} else {
            		returnFileList(proj,projectPath,xName, request);
            	}
    		}
    	}
	}

	@Override
	public void findTriageByProjectIdAndXname(UserI user, String projectId, String xName, String file, HttpServletRequest request, String compression) throws InvalidItemException, NotFoundException, InsufficientPrivilegesException, ActionException, Exception {
		String projectPath=TriageUtils.getTriageProjectPath(projectId);
		MediaType mt = MediaType.parseMediaType(request.getContentType());
		XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId,user, false);
    	if(proj!=null && proj.canRead(user)){
    		if(xName != null) {
            	if (isZIPRequest(mt)) {
            		returnZippedFiles(proj,projectPath,xName, user, request,compression);
            	} else {
            		returnFile(proj,projectPath,xName, file, user);
            	}
    		}
    	}
	}
	
	@Override
	public void create(UserI user, String projectId, String xname, String file,String eventReason, String eventComment, String eventId,String target,boolean inbody, String overwrite,String format,String content,String event_reason,String extract,HttpServletRequest request) {
		try {
		String projectPath = TriageUtils.getTriageUploadsPath();
		if( projectId==null){
        	//fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation."); //Pending IMPL
        } 
		 XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
     	if(proj!=null && proj.canRead(user) && canEditDestination(target, user)){
		        if (xname == null && file == null) {
		        	//fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation.");
			    } else if (xname != null && file == null) {
			        uploadTriageFile(projectPath,xname, null, inbody,target,overwrite,format,content,event_reason,extract, request,user);
			    }else if (xname != null && file != null) {
			        uploadTriageFile(projectPath,getxName(projectId,request),file, inbody,target,overwrite,format,content,event_reason,extract,request,user);
			    }
	        	openworkflow(true,"Upload Quarantine Files", eventReason, eventComment,user, projectId);
	        	
	        	//XNATCR-834: stops IE opening save dialog
	        	//this.getResponse().setEntity("",MediaType.TEXT_HTML); //Pending IMPL
     	}else{
     		//fail(Status.CLIENT_ERROR_UNAUTHORIZED,"Not authorized");
     	}
		} catch (Exception e) {
			//fail(Status.SERVER_ERROR_INTERNAL,e.getMessage());
			log.error("",e);
		}
	}
	
	public void openworkflow(boolean status,String action,String reason,String comment, UserI user, String projectId) throws Exception{
		PersistentWorkflowI work=WorkflowUtils.buildOpenWorkflow( user, "xnat:projectData", projectId, projectId,EventUtils.newEventInstance(EventUtils.CATEGORY.DATA,EventUtils.TYPE.WEB_FORM,  action,reason,comment));
		if(status){
			WorkflowUtils.complete(work, work.buildEvent());		
		}else{
			WorkflowUtils.fail(work, work.buildEvent());
		}
	}
	
	private boolean uploadTriageFile(String projectPath,String xname,String file, boolean inbody,String target,String overwrite,String format,String content,String event_reason, String extract, HttpServletRequest request, UserI user) throws InitializationException {

		// Create any subdirectories requested as well
		String fileName=null;
		File directory=null;
		File resourceFile=new File(projectPath + File.separator + xname);
		fileName=resourceFile.getName();
		directory=resourceFile.getParentFile();
		log.warn("resourceFile:"+resourceFile.getAbsolutePath());
		log.warn("directory:"+directory.getAbsolutePath());

		
		//Upload to non-existing resources (auto-create) or fail?  Should auto-create.
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new InitializationException("Could not create resource directory.");
			}
		}
		
		saveTriageManifestFile(directory.getAbsolutePath(),fileName,target,overwrite,format,content,event_reason,request,user);
		
		
		if(inbody){
			return handleInbodyTriageFileUpload(projectPath,directory.getAbsolutePath(),fileName);
		} else {
			return handleAttachedTriageFileUpload(projectPath,directory.getAbsolutePath(),fileName, extract);
		}
		
	}
	
  private boolean handleInbodyTriageFileUpload(String projectPath, String dirString, String fileName) throws InitializationException {
		try {
			
			// This is probably redundant due to current doPut/doPost coding, but including it anyway.
			if (fileName==null || fileName.length()<1) {
	        	throw new DataFormatException("Please use HTTP PUT request to specify a file name in the URL.");
			}
	        
	        // Write original file if not requesting or have non-archive file
			File ouf=new File(dirString,fileName);
			
			ouf.getParentFile().mkdirs();
			
			//(new FileWriterWrapper(this.getRequest().getEntity(),fileName)).write(ouf);  //Pending IMPL
	        
			log.warn("fileName "+fileName);
			log.warn("dirString "+dirString);
			

	        return true;
			
		} catch (Exception e) {
			log.error("",e);
			throw new InitializationException(e.getMessage());
		}
		
	}

  private Map<String,String> bodyParams=Maps.newHashMap();
  private boolean handleAttachedTriageFileUpload(String projectPath, String dirString, String requestedName, String extract) throws InitializationException {
	
	org.apache.commons.fileupload.DefaultFileItemFactory factory = new org.apache.commons.fileupload.DefaultFileItemFactory();
	org.restlet.ext.fileupload.RestletFileUpload upload = new  org.restlet.ext.fileupload.RestletFileUpload(factory);

    List<FileItem> fileItems = null;
	try {
		
		//fileItems = upload.parseRequest(this.getRequest()); // Pending IMPL

		for (FileItem fi:fileItems) {    						         
	    	
			if (fi.isFormField()) {
            	// Load form field to passed parameters map
				bodyParams.put(fi.getFieldName(),fi.getString());
               	continue;
            } 
			
	        String fileName;
			if (requestedName==null || requestedName.length()<1) {
				fileName=fi.getName();
			} else {
				fileName=requestedName;
			}
			
			//sfinal String extract=this.retrieveParam("extract");
	        /*if (extract!=null && extract.equalsIgnoreCase("true")) {
	        	// Write extracted files
	        	CompressionMethod method = getCompressionMethod(fileName);
	        	if (method != CompressionMethod.NONE) {
	        		if (!extractCompressedFile(fi.getInputStream(),dirString,fileName,method)) {
	        			this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,"Error extracting file.");
	        			return false;
	        		} else {
	        			// If successfully extracted, don't create unextracted file
	        			continue;
	        		}
	        	}
	        }*/
        	fi.write(new File(dirString + "/" + fileName));
		
	    }
		return true;
    
	} catch (Exception e) {
		log.error("",e);
		throw new InitializationException(e.getMessage());
	}
}
	
	private boolean saveTriageManifestFile(String path,String name, String target,String overwrite,String format,String content,String event_reason, HttpServletRequest request, UserI user) {
		boolean success=false;
		
		File manifestFile;
		File resourceFile;
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			if(StringUtils.isNotEmpty(name)){
				name = StringUtils.stripEnd(name,"/");
				resourceFile=new File(path+File.separator+name);
				manifestFile=new File(path+File.separator+MANIFEST);
			}else{
				resourceFile=new File(path);
				manifestFile=this.getManifestFileFromResource(resourceFile);
			}
			log.warn(manifestFile.getPath());
			log.warn(resourceFile.getPath());
			
			TriageManifest tManifest=(manifestFile.exists())?mapper.readValue(manifestFile, TriageManifest.class): new TriageManifest();
			
			Map<String, String> entry=new HashMap<String,String>();
			entry.put("Resource", constructResourceURI(resourceFile.getName(),request));
			entry.put("URI", constructResourceURI(resourceFile.getName(), request));
			entry.put("TARGET", constructTargetURI(resourceFile.getName(), target, request));
			entry.put("FTARGET", constructFormattedTargetURI(resourceFile.getName(),target,request, user));
			//entry.put("FSOURCE", constructResourceNames(resourceFile.getName()));

			entry.put("USER",user.getUsername());
			entry.put("DATE",(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format( Calendar.getInstance().getTime()));

			//entry.put("DATE",java.util.Calendar.getInstance(java.util.TimeZone.getDefault()).getTime().toString());
			entry.put(OVERWRITE,constructOverwrite(overwrite));
			entry.put(EVENT_REASON,constructEventReason(event_reason));
			
			entry.put(FORMAT,constructFormat(format));
			entry.put(CONTENT,constructContent(content));
			
			tManifest.addEntry(entry);
			mapper.writeValue(manifestFile, tManifest);
			
			
			success=true;
		} catch (JsonGenerationException e) {
			log.warn(e.getMessage());
			success=false;
		} catch (JsonMappingException e) {
			log.warn(e.getMessage());
			success=false;

		} catch (IOException e) {
			log.warn(e.getMessage());
			success=false;
		}

		return success;
	}
	
	  private String constructOverwrite(String overwrite) {
		    if (StringUtils.isEmpty(overwrite)) {
		    	overwrite="false";
		    }
		    return overwrite;
	     }
	    private String constructContent(String content) {
		    if (StringUtils.isEmpty(content)) {
		    	content="";
		    }
		   
		    return content;
	     }
	    private String constructFormat(String format) {
		    if (StringUtils.isEmpty(format)) {
		    	format="";
		    }
		    return format;
	     }
	    
	    private String constructEventReason( String event_reason) {
	 	    if (StringUtils.isEmpty(event_reason)) {
	 	    	event_reason="";
	 	    }
	 	    return event_reason;
	      }
	
	 private String constructTargetURI(String resource, String target, HttpServletRequest request) {
		    if (StringUtils.isEmpty(target)) {
		    	//construct target from url
		    	String requestPart = request.getServletPath() + request.getPathInfo();
		    	requestPart= this.appendFiles(requestPart, resource);
		    	//replace triage with archive
		    	target=requestPart.replace(TRIAGE, ARCHIVE);
		    }
		    if(!target.startsWith(DATA)){
		    	target=target.substring(target.indexOf(DATA));
		    }
		    if(!target.endsWith(FILES)){
		    	target=target+File.separator+FILES;
		    }
		    return target;
	     }
	 
	 public  String constructFormattedTargetURI(String resource, String target, HttpServletRequest request, UserI user) {
    	 String formatted=constructTargetURI(resource, target,request);
    	 //need to format IDs to Labels for user.
    	
    	 String[] params = StringUtils.split(formatted, "/");
    	 for(int i=0;i<params.length-1;i++){
    		 if("experiments".equals(params[i])){
    			 String expid=params[i+1];
    			 XnatExperimentdata exp=XnatExperimentdata.getXnatExperimentdatasById(expid, user, false);
    			 if(exp!=null){
    		    	 formatted=formatted.replace(expid,exp.getLabel());
    			 }
    		 }
    		 if("subjects".equals(params[i])){
    			 String subjid=params[i+1];
    			 XnatSubjectdata subj=XnatSubjectdata.getXnatSubjectdatasById(subjid, user, false);
    			 if(subj!=null){
    		    	 formatted=formatted.replace(subjid,subj.getLabel());
    			 }
    		 }
    		 if("assessors".equals(params[i])){
    			 String assid=params[i+1];
    			 XnatExperimentdata exp=XnatExperimentdata.getXnatExperimentdatasById(assid, user, false);
    			 if(exp!=null){
    		    	 formatted=formatted.replace(assid,exp.getLabel());
    			 }
    		 }
    	 }
	   
    	 formatted=formatted.replace("/data/archive/projects/","<b>Project</b>: ");
    	 formatted=formatted.replace("/subjects/","<br><b>Subject</b>: ");
    	 formatted=formatted.replace("/data/archive/experiments/","<b>Session</b>: ");
    	 formatted=formatted.replace("/assessors/","<br><b>Assessor</b>: ");
    	 formatted=formatted.replace("/scans/","<br><b>Scan</b>: ");
    	 formatted=formatted.replace("/resources/","<br><b>Resource</b>: ");
    	 formatted=formatted.replace("/resources","<br><b>Resource</b>: ");
    	 formatted=formatted.replace("/files/","");
    	 formatted=formatted.replace("/files","");
    	
    	 return formatted;
     }
	
	
	private boolean canEditDestination(String target, UserI user) throws Exception {
		String targetResource = target.replaceAll("(/files)?$", "/files");
		ResourceURII arcURI = convertValue(targetResource);
		return arcURI.getSecurityItem().canEdit(user);
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
	
	String getxName(String project, HttpServletRequest request){
		String requestPart = this.mapToProjectResources(request.getServletPath() + request.getPathInfo());

		int bindex =StringUtils.indexOf(requestPart, "/projects/"+project);
		//only compress urls for subject and deeper
		int lindex =StringUtils.lastIndexOf(requestPart, "/resources");
		String xName=requestPart.substring(bindex);
		//mapped=originalResourceUrl.replace(extra, "");
		
		return xName;
	}
	
//	private boolean uploadTriageFile(String projectPath,String pXNAME) {
//		return uploadTriageFile(projectPath,pXNAME,null);
//	}
	
private void returnFile(XnatProjectdata proj, String projectPath,String xname,String file, UserI user) throws InvalidItemException, Exception {
		
		String escapedPath=projectPath+File.separator+ File.separator+"resources"+File.separator+xname+File.separator+"files"+File.separator+file;
		String resourcePath=projectPath+File.separator+ File.separator+"resources";
		String path=URLDecoder.decode(escapedPath, "UTF-8");
		File reqFile = new File (path);
		if (reqFile.exists() && reqFile.isFile() && canRead(proj, new File(escapedPath), user)) {
			//sendFileRepresentation(reqFile);
		} else {
			throw new NotFoundException("Quarantine file not found.");
		}
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
	
	private void returnFileList(XnatProjectdata xproj, String projectPath, String xName, HttpServletRequest request) {
		List<TriageFileUtil> response = new  ArrayList<>();
		File dir = new File (projectPath+File.separator+ File.separator+"resources"+File.separator+xName+File.separator+"files");
		//need to ignore .json files.
		if (dir.exists() && dir.isDirectory()) {
			ArrayList<File> fileList = new ArrayList<File>();
			fileList.addAll(FileUtils.listFiles(dir,null,true));
			Iterator<File> i = fileList.iterator();
	        while (i.hasNext()) {
	        	File f = i.next();
	        	if(!MANIFEST.equals(f.getName())){
	        		String fileRelativeName= relative(dir, f);
	        		response.add(getTriageFileData(fileRelativeName, f, request));
	        	}
	        }
		}
	}
	
	private TriageFileUtil getTriageFileData(String fileRelativeName, File f, HttpServletRequest request) {
		return TriageFileUtil.builder()
				 .name(fileRelativeName)
				 .uri(constructURI(fileRelativeName, request))
				 .target(getPropertyFromManifest(f, TARGET))
				 .user(getPropertyFromManifest(f, USER))
				 .date(getPropertyFromManifest(f, DATE))
				 .overwrite(getPropertyFromManifest(f, OVERWRITE))
				 .eventReason(getPropertyFromManifest(f, EVENT_REASON))
				 .ftarget(getPropertyFromManifest(f, FTARGET))
				 .format(getPropertyFromManifest(f, FORMAT))
				 .content(getPropertyFromManifest(f, CONTENT))
				 .size(f.length()).build();
	}
	
    private String constructURI(String resource, HttpServletRequest request) {
    	String requestPart = this.mapToProjectResources(request.getServletPath() + request.getPathInfo());
 
    	return appendFiles(requestPart, resource);
    	
    }
    private String appendFiles(String requestPart,String resource){
    	if (!requestPart.endsWith(resource) && !requestPart.endsWith("/files") && !requestPart.endsWith("/files/")) {
    		requestPart+="/files";
    	}
    	if (!requestPart.endsWith(resource)){
    		requestPart+=File.separator+resource;
    	}
    	return requestPart;
    }

	public String relative( final File base, final File file ) {
	    final int rootLength = base.getAbsolutePath().length();
	    final String absFileName = file.getAbsolutePath();
	    final String relFileName = absFileName.substring(rootLength + 1);
	    return relFileName;
	}

	private void returnZippedFiles(XnatProjectdata xproj, String projectPath, String xName,UserI user, HttpServletRequest request, String compression) throws InvalidItemException, NotFoundException, InsufficientPrivilegesException, ActionException, Exception {
		String dirPath = File.separator+"resources"+File.separator+xName+File.separator+"files";
		String resourcePath=projectPath+File.separator+RESOURCES+File.separator+xName;
		if(canRead(xproj, new File(resourcePath),user)){
			File dir = new File (projectPath,dirPath);
			if (dir.exists() && dir.isDirectory()) {
				ArrayList<File> fileList = new ArrayList<File>();
				fileList.addAll(FileUtils.listFiles(dir,null,true));
				sendZippedFiles(projectPath,xName,xName,fileList, request, compression);
			} else {
				throw new NotFoundException("Quarantine directory not found or is not a directory.");
			}
		}else{
			throw new InsufficientPrivilegesException("Not authorized");
		}
	}
	
	private void sendZippedFiles(String projectPath,String pXNAME,String fileName,ArrayList<File> fileList, HttpServletRequest request, String compression) throws ActionException {
		
		ZipRepresentationUtil zRep;
		if(MediaTypeUtil.getRequestedMediaType(request.getContentType())!=null && MediaTypeUtil.getRequestedMediaType(request.getContentType()).equals(MediaTypeUtil.APPLICATION_GNU_TAR)){
			zRep = new ZipRepresentationUtil(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_GNU_TAR),projectPath,ZipOutputStream.DEFLATED);
			setContentDisposition(String.format("%s.tar.gz", fileName));
		}else if(MediaTypeUtil.getRequestedMediaType(request.getContentType())!=null && MediaTypeUtil.getRequestedMediaType(request.getContentType()).equals(MediaTypeUtil.APPLICATION_TAR)){
			zRep = new ZipRepresentationUtil(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_TAR),projectPath,ZipOutputStream.STORED);
			setContentDisposition(String.format("%s.tar.gz", fileName));
		}else{
			zRep = new ZipRepresentationUtil(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_ZIP), projectPath, identifyCompression(null, compression));
			setContentDisposition(String.format("%s.zip", fileName));
		}
		zRep.addAllAtRelativeDirectory(projectPath,fileList);
		//this.getResponse().setEntity(zRep);
	}

	private String setContentDisposition(String fileName) {
		return String.format(ATTACHMENT_DISPOSITION, fileName);
	}

	public Integer identifyCompression(Integer defaultCompression, String compression) throws ActionException {
		try {
			if (StringUtils.isNoneBlank(compression)) {
				return Integer.valueOf(compression);
			}
		} catch (NumberFormatException e) {
			throw new ClientException(e.getMessage());
		}

		if (defaultCompression != null) {
			return defaultCompression;
		} else {
			return ZipUtils.DEFAULT_COMPRESSION;
		}
	}
	
	boolean canRead(XnatProjectdata proj,File f, UserI user) throws InvalidItemException, Exception{
		boolean allowed=false;
		if (Features.checkFeature(user,proj.getSecurityTags().getHash().values(), "QuarantineReview") || StringUtils.equals(user.getUsername(),getUser(f))){
            allowed= true;
        }else{
        	allowed= false;
        }
		return allowed;
	}

	
	private static boolean isZIPRequest(MediaType mt) {
		MediaType zip = MediaType.parseMediaType(MediaTypeUtil.APPLICATION_ZIP);
		MediaType tar = MediaType.parseMediaType(MediaTypeUtil.APPLICATION_TAR);
		return !(mt == null || !(mt.equals(zip)) || mt.equals(tar));
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
	private final String TRIAGE="/services/triage/";
	private final String ARCHIVE="/archive/";
	private final String RESOURCE ="Resource";
	private final String DATA ="/data";
	private final String FSOURCE ="FSOURCE";
	private static final String _ON_FAILURE_RETURN_JS = "_onFailureReturnJS";
	private static final String _ON_FAILURE_RETURN_HTML = "_onFailureReturnHTML";
	private static final String COMPRESSION = "compression";
	private static final String ATTACHMENT_DISPOSITION = "attachment; filename=\"%s\"";
	
}
