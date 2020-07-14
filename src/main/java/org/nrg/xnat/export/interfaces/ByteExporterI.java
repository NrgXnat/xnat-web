package org.nrg.xnat.export.interfaces;

import java.io.File;
import java.util.Map;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.transformers.TransformerHelper;
import org.nrg.xnat.export.transporters.http.HTTPResponseHolder;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface ByteExporterI {

	void setup(Map<String, Object> setupParams) throws Exception;
	HTTPResponseHolder exportToDestination(String projectRootPath, File fileToExport, final TransformerHelper transformerHelper) throws Exception; 
}
