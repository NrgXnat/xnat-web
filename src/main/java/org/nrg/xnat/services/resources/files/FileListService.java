/**
 * 
 */
package org.nrg.xnat.services.resources.files;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileUploadException;
import org.nrg.action.ClientException;
import org.nrg.xft.XFTTable;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.restlet.data.MediaType;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public interface FileListService {

	public boolean allowPut() ;

    public boolean allowPost() ;

    public boolean allowDelete() ;

    public Representation represent(Variant variant);
	
    public void handlePut() ;

    public void handlePost();
    
    public void handleDelete();
    
    public Representation representTable(final XFTTable table, final MediaType mediaType, final Hashtable<String, Object> parameters, final Map<String, Map<String, String>> columnProperties, final Map<String, String> sessionMapping);
    
    public List<FileWriterWrapperI> getFileWritersAndLoadParams(final Representation entity, boolean useFileFieldName) throws FileUploadException, ClientException;
    
}
