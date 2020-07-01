package org.nrg.xnat.export.interfaces;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface TransformerI {

	boolean transform(File inFile, File outDir);
	
}
