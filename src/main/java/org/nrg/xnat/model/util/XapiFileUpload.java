package org.nrg.xnat.model.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public class XapiFileUpload extends FileUpload  {
	/**
     * Constructs an uninitialised instance of this class. A factory must be
     * configured, using <code>setFileItemFactory()</code>, before attempting to
     * parse request entity.
     * 
     * @see RestletFileUpload#RestletFileUpload(FileItemFactory)
     */
    public XapiFileUpload() {
        super();
    }

    /**
     * Constructs an instance of this class which uses the supplied factory to
     * create <code>FileItem</code> instances.
     * 
     * @see RestletFileUpload#RestletFileUpload()
     */
    public XapiFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }
    
    /**
     * Processes an <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>
     * compliant <code>multipart/form-data</code> input representation.
     * 
     * @param request
     *            The request containing the entity to be parsed.
     * @return A list of <code>FileItem</code> instances parsed, in the order
     *         that they were transmitted.
     * @throws FileUploadException
     *             if there are problems reading/parsing the request or storing
     *             files.
     * @throws IOException 
     */
    public List<FileItem> parseRequest(Resource file)  throws FileUploadException, IOException {
    	List<FileItem> items = new ArrayList<FileItem>();
    	final File temporary = File.createTempFile("upload", ".tmp");
    	DiskFileItemFactory factory = new DiskFileItemFactory();
    	FileItem item = factory.createItem("file", temporary.getAbsolutePath(), false,
    			temporary.toString());
    	items.add(item);
       // return parseRequest(file);
		return items;
    }

}
