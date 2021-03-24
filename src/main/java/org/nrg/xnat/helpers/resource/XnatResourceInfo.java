/*
 * web: org.nrg.xnat.helpers.resource.XnatResourceInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.restlet.util.FileWriterWrapperI.UPLOAD_TYPE;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class XnatResourceInfo implements Serializable, FileWriterWrapperI {
    private static final long serialVersionUID = 42L;
	private String description,format,content, name, nestedPath,rename=null;
	private Long fileSize;
	private Number event_id=null;
	private List<String> tags= new ArrayList<>();
	private Map<String,String> meta= new HashMap<>();
	private final Date lastModified,created;
	private final UserI user;
	private InputStreamResource resource;
	private File file;
	
	public Date getLastModified() {
		return lastModified;
	}
	public Date getCreated() {
		return created;
	}
	public UserI getUser() {
		return user;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(final String description) {
		this.description = description;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(final String format) {
		this.format = format;
	}
	public String getContent() {
		return content;
	}
	public void setContent(final String content) {
		this.content = content;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setMeta(final Map<String, String> meta) {
		this.meta = meta;
	}
	
	public void addMeta(final String key, final String value){
		this.meta.put(key, value);
	}
	public Map<String, String> getMeta() {
		return meta;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void addTag(String tag) {
		this.tags.add(tag);
	}
	
	public String getRename() {
		return rename;
	}
	public void setRename(String rename) {
		this.rename = rename;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	public InputStreamResource getResource() {
		return resource;
	}
	public void setResource(InputStreamResource resource) {
		this.resource = resource;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNestedPath(String nestedPath) {
		this.nestedPath = nestedPath;
	}
	public XnatResourceInfo(UserI user, Date created, Date lastModified){
		this.created=created;
		this.lastModified=lastModified;
		this.user=user;
		
	}
	
	public XnatResourceInfo(UserI user, Date created, Date lastModified, InputStreamResource resource,  File file,  String name) {
		this.created=created;
		this.lastModified=lastModified;
		this.user=user;
		this.resource = resource;
        this.name = name;
        this.nestedPath = null;
        this.file = file;
	}

	
    public static XnatResourceInfo buildResourceInfo(final String description, final String format, final String content, final String[] tags, final UserI user, Date created, Date modified, Number i){
		XnatResourceInfo info = new XnatResourceInfo(user,created,modified);
        
	    if(description!=null){
	    	info.setDescription(description);
	    }
	    if(format!=null){
	    	info.setFormat(format);
	    }
	    if(content!=null){
	    	info.setContent(content);
	    }
	    
	    if(i!=null){
	    	info.setEvent_id(i);
	    }
	    
	    if(tags!=null){
	    	for(String tag: tags){
	    		tag = tag.trim();
	    		if(StringUtils.isNotBlank(tag)){
	    			for(String s: tag.split("[\\s]*,[\\s]*")){
	    				s=s.trim();
	    				if(!s.equals("")){
	    		    		if(s.contains("=")){
	    		    			info.addMeta(s.substring(0,s.indexOf("=")),s.substring(s.indexOf("=")+1));
	    		    		}else{
	    		    			if(s.contains(":")){
		    		    			info.addMeta(s.substring(0,s.indexOf(":")),s.substring(s.indexOf(":")+1));
		    		    		}else{
		    		    			info.addTag(s);
		    		    		}
	    		    		}
	    				}
	    			}
	    			
	    		}
	    	}
	    }
		
		return info;
	}
	public void setEvent_id(Number event_id) {
		this.event_id = event_id;
	}
	public Number getEvent_id() {
		return event_id;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getNestedPath() {
		return nestedPath;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		 return resource.getInputStream();
	}

	@Override
	public void delete() {
		if (file != null) {
			file.delete();
		}
	}

	@Override
	public UPLOAD_TYPE getType() {
		 return null == resource ? FileWriterWrapperI.UPLOAD_TYPE.MULTIPART : FileWriterWrapperI.UPLOAD_TYPE.INBODY;
	}
	
	@Override
	public void write(File file) throws Exception {
		if (null != resource) {
            final FileOutputStream fw = new FileOutputStream(file);
            IOException ioexception = null;
            try {
                if (file.length() >2000000) {
                    IOUtils.copyLarge(resource.getInputStream(), fw);
                } else {
                    IOUtils.copy(resource.getInputStream(), fw);
                }
            } catch (IOException e) {
                throw ioexception = e;
            } finally {
                try {
                    fw.close();
                } catch (IOException e) {
                    throw null == ioexception ? e : ioexception;
                }
            }
        } else {
        	throw new FileNotFoundException("File is empty");
        }
	}

}
