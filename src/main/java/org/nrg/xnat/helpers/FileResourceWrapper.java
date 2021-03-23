package org.nrg.xnat.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.springframework.core.io.InputStreamResource;

public class FileResourceWrapper implements FileWriterWrapperI {

	private final InputStreamResource resource;
	private final File file;
	private final String name;
	private final String nestedPath;
	
	public FileResourceWrapper(final InputStreamResource resource, final File file, final String name) {
		 	this.resource = resource;
	        this.name = name;
	        this.nestedPath = null;
	        this.file = file;
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
		if (null == resource) {
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
        } 
	}

}
