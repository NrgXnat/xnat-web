package org.nrg.xnat.services.resources.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oro.io.GlobFilenameFilter;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.services.cache.UserDataCache;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXWriter;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.zip.ZipUtils;
import org.nrg.xnat.dto.resource.DIRResourceDto;
import org.nrg.xnat.dto.resource.FileSet;
import org.nrg.xnat.dto.resource.MediaTypeUtil;
import org.nrg.xnat.dto.resource.ZipRepresentationUtil;
import org.nrg.xnat.services.resources.DIRResourceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DIRResourceServiceImpl implements DIRResourceService {

	@Override
	public List<DIRResourceDto> findAllDIRResources(UserI user, String projectId, String experimentId, String filepath, boolean recursive, boolean isXarReference) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters {
		
		List<DIRResourceDto> response = new ArrayList<>();
		
		XnatProjectdata proj = getXnatProject(projectId, user);
		
		XnatExperimentdata expt = getXnatExperiment(experimentId, proj, user);
		
		if (user.isGuest()) {
			throw new NotAuthenticatedException("");
		}
		if(expt instanceof XnatSubjectassessordata){
			if(filepath==null){
				filepath="";
			}
			final File session_dir=expt.getSessionDir();
			if(session_dir==null){
				throw new NotFoundException("Session directory doesn't exist in standard location for this experiment.");
			}
			try {
				final List<File> src = getSourceFile(filepath, session_dir);
				
				if (!(src.size() == 1 && !src.get(0).isDirectory())) {
					final List<FileSet> dest = getFileSet(src, recursive, isXarReference);
					response =  getDIRResourceResult(dest, session_dir, expt);
				}
			} catch (InvalidFileCharacters e) {
				throw new InvalidFileCharacters(String.format("'%s' is not allowed in this resource URI.",e.characters));
			}
		}
		return response;
	}
	
	@Override
	public StreamingResponseBody findAllXARResources(UserI user, String projectId, String experimentId, String filepath, boolean recursive, boolean isXarReference, HttpServletRequest sRequest, HttpHeaders hRequest, String compression) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters, InitializationException {
		
		//MediaType mediaType = hRequest.getContentType() != null? hRequest.getContentType(): MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR);
		
		MediaType mediaType =  MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR);
		
		XnatProjectdata proj = getXnatProject(projectId, user);
		
		XnatExperimentdata expt = getXnatExperiment(experimentId, proj, user);
		
		if (user.isGuest()) {
			throw new NotAuthenticatedException("");
		}
		if(expt instanceof XnatSubjectassessordata){
			if(filepath==null){
				filepath="";
			}
			final File session_dir=expt.getSessionDir();
			if(session_dir==null){
				throw new NotFoundException("Session directory doesn't exist in standard location for this experiment.");
			}
			try {
				final List<File> src = getSourceFile(filepath, session_dir);
				
				if (src.size() == 1 && !src.get(0).isDirectory()) {
					final File f=src.get(0);
					if (isZIPRequest(mediaType)) {
						zipFileRequest(mediaType, expt, f, compression);
					}
				}else{
					final List<FileSet> dest = getFileSet(src, recursive, isXarReference);
					if ((isZIPRequest(mediaType) || (mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR))))) {
						ZipRepresentationUtil rep = zipRepresentation(mediaType, expt, user,sRequest, compression );
						for (final FileSet fileSet : dest) {
							rep.addAll(fileSet.getMatches());
						}
						FILE_NAME = rep.getDownloadName();
						return rep;
					}
				}
			} catch (InvalidFileCharacters e) {
				throw new InvalidFileCharacters(String.format("'%s' is not allowed in this resource URI.",e.characters));
			}
		}
		return null;
	}
	
	 
	@Override
	public String getContentDisposition() {
		return String.format(ATTACHMENT_DISPOSITION, FILE_NAME);
	}

	public static class InvalidFileCharacters extends Exception {
		public String characters;

		public InvalidFileCharacters(String chars) {
			characters = chars;
		}
	}

	public static boolean isZIPRequest(MediaType mt) {
		MediaType zip = MediaType.parseMediaType(MediaTypeUtil.APPLICATION_ZIP);
		MediaType tar = MediaType.parseMediaType(MediaTypeUtil.APPLICATION_TAR);
		return !(mt == null || !(mt.equals(zip)) || mt.equals(tar));
	}
	
	private List<DIRResourceDto> getDIRResourceResult(List<FileSet> dest, File session_dir, XnatExperimentdata expt) {
		List<DIRResourceDto> response = new ArrayList<>();
		for(final FileSet fs:dest){
			final File parent=fs.getParent();
			for(final File f:fs.getMatches()){
				final String rel=(session_dir.toURI().relativize(f.toURI())).getPath();
				response.add(DIRResourceDto.builder()
						.DIR(f.isDirectory())
						.size(f.length())
						.name(parent.toURI().relativize(f.toURI()).getPath())
						.URI(String.format("/data/experiments/%1$s/DIR/%2$s%3$s", expt.getId(), rel, f.isDirectory() ? "?format=json" : ""))
						.build());
			}
		}
		return response;
	}

	private List<File> getSourceFile(String filepath, File session_dir) throws InvalidFileCharacters, NotFoundException {
		List<File> src = new ArrayList<>();
		if(filepath.equals("")){
			src= new ArrayList<>();
			src.add(session_dir);
		}else{
			src=getFiles(session_dir,filepath,true);
		}
		if(src.size()==0){
			throw new NotFoundException("Specified request didn't match any stored files.");
		}
		return src;
	}

	private XnatExperimentdata getXnatExperiment(String experimentId, XnatProjectdata proj, UserI user) throws NotFoundException {
		XnatExperimentdata expt= null;
		if (StringUtils.isNotBlank(experimentId)) {
			expt=XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
			if(Objects.isNull(expt) && Objects.nonNull(proj)){
				expt= XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), experimentId, user, false);
			}
		}
		if(Objects.isNull(expt)) {
			throw new NotFoundException("");
		}
		return expt;
	}

	private XnatProjectdata getXnatProject(String projectId, UserI user) {
		XnatProjectdata proj = null;
		if(StringUtils.isNotBlank(projectId)) {
			proj = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		}
		return proj;
	}

	private List<FileSet> getFileSet(List<File> src, boolean recursive, boolean isXarReference) {
		final List<FileSet> dest= new ArrayList<>();
		for(File f: src){
			final FileSet set=new FileSet(f);
			if(f.isDirectory()){
				if(recursive || isXarReference) { 
					set.addAll(FileUtils.listFiles(f, null, true));
				}else{
					File[] children=f.listFiles();
					if(children!=null){
						set.addAll(Arrays.asList(children));
					}
				}
			}else{
				set.add(f);
			}
			dest.add(set);
		}
		return dest;
	}

	private ZipRepresentationUtil zipFileRequest(MediaType mediaType, XnatExperimentdata expt, File f, String compression) {
		if(isZIPRequest(mediaType)){
			ZipRepresentationUtil rep = null;
			try{
				rep=new ZipRepresentationUtil(mediaType,Collections.singletonList((expt).getArchiveDirectoryName()),identifyCompression(null, compression));
			} catch (ActionException e) {
				log.error("", e);
			}
			rep.addEntry(f);
			FILE_NAME = String.format("%s.zip", f.getName());
			return rep;
		}else{
			//return this.representFile(f, mediaType);
		}
		return null;
	}

	private ZipRepresentationUtil zipRepresentation(MediaType mediaType, XnatExperimentdata expt, UserI user, HttpServletRequest request, String compression ) throws InitializationException {
		ZipRepresentationUtil rep = null;
		try{
			rep=new ZipRepresentationUtil(mediaType,Collections.singletonList((expt).getArchiveDirectoryName()),identifyCompression(null, compression));
		} catch (ActionException e) {
			log.error("", e);
		}
		if (mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR))) {
			final File output = getUserDataCache().getUserDataCacheFile(user, Paths.get("expt_" + new Date().getTime()), UserDataCache.Options.DeleteOnExit, UserDataCache.Options.Overwrite);
			log.info("Getting ready to write item XML to the file {}", output.getAbsolutePath());

			try (final OutputStream outputStream = new FileOutputStream(output)) {
				final SAXWriter writer = new SAXWriter(outputStream, true);
				writer.setAllowSchemaLocation(true);
				writer.setLocation(TurbineUtils.GetRelativePath(request) + "/" + "schemas/");
				writer.setRelativizePath(expt.getArchiveDirectoryName() + "/");
				writer.write(expt.getItem());
				rep.addEntry(expt.getId() + ".xml", output);
			} catch (Exception e) {
				throw new InitializationException("Unable to retrieve/save session XML.");
			}
	}
		return rep;
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
	
	protected UserDataCache getUserDataCache() {
		_userDataCache = XDAT.getContextService().getBean(UserDataCache.class);
		return _userDataCache;
	}

	public static List<File> getFiles(File dir,String path,boolean recursive) throws InvalidFileCharacters{
		final List<File> files= new ArrayList<>();
		final int slash=path.indexOf("/");
		if(slash>-1){
			final String local=path.substring(0,slash);
			
			if(path.length()>(slash+1)){
				path=path.substring(slash+1);
			}else{
				recursive=false;
			}
			
			if(path.trim().equals("..")){
				throw new InvalidFileCharacters("..");
			}
			
			final GlobFilenameFilter glob = new GlobFilenameFilter(local);
			final String[] children=dir.list(glob);
			if (children != null) {
				for(final String child:children){
					final File f=new File(dir,child);
					if(recursive && f.isDirectory()){
						files.addAll(getFiles(f,path,true));
					}else{
						files.add(f);
					}
				}
			}

		}else{
			if(path.trim().equals("..")){
				throw new InvalidFileCharacters("..");
			}
			final GlobFilenameFilter glob = new GlobFilenameFilter((path.equals(""))?"*":path);
			final String[] children=dir.list(glob);
			if (children != null) {
				for (final String child : children) {
					files.add(new File(dir, child));
				}
			}
		}
		
		return files;
	}
	
	 private UserDataCache _userDataCache;
	 private static final String ATTACHMENT_DISPOSITION = "attachment; filename=\"%s\"";
	 private static String FILE_NAME = "";

}