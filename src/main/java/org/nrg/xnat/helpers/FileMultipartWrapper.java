package org.nrg.xnat.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.springframework.web.multipart.MultipartFile;

public class FileMultipartWrapper implements FileWriterWrapperI {

	private final MultipartFile fi;
	private final File f;
	private final String name;
	private final String nestedPath;
	
	public FileMultipartWrapper(final MultipartFile fi, final File f, final String name) {
		 	this.fi = fi;
	        this.name = name;
	        this.nestedPath = null;
	        this.f = f;
	}
	
	
	@Override
	public void write(File f) throws Exception {
		if (null == fi) {
            final FileOutputStream fw = new FileOutputStream(f);
            IOException ioexception = null;
            try {
                if (fi.getSize()>2000000) {
                    IOUtils.copyLarge(fi.getInputStream(), fw);
                } else {
                    IOUtils.copy(fi.getInputStream(), fw);
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
		 return fi.getInputStream();
	}

	@Override
	public void delete() {
		if (f != null) {
			f.delete();
		}
	}

	@Override
	public UPLOAD_TYPE getType() {
		 return null == fi ? FileWriterWrapperI.UPLOAD_TYPE.MULTIPART : FileWriterWrapperI.UPLOAD_TYPE.INBODY;
	}

}
