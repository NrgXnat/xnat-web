package org.nrg.xnat.export.interfaces;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.nrg.xnat.export.exception.FailedToTransformException;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface TransformerI {

	void init(Map<String, Object> params);
	boolean transform(File inFile, File outDir) throws FailedToTransformException;
	boolean transform(InputStream inFile, OutputStream outDir) throws FailedToTransformException;
	
}
