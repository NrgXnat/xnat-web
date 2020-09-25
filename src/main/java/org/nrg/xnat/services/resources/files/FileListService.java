/**
 * 
 */
package org.nrg.xnat.services.resources.files;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileUploadException;
import org.nrg.action.ClientException;
import org.nrg.xft.XFTTable;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.restlet.data.MediaType;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public interface FileListService {

	public String getResourceFiles(UserI sessionUser, String assessedId, String scanId);

	public String getResources(UserI sessionUser, String assessedId, String scanId) throws IOException;
    
}
