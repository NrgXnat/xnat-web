package org.nrg.xnat.export.interfaces;

import java.io.File;
import java.io.InputStream;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xft.security.UserI;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface ExportAnonymizerI {
	boolean anonymize(XnatAbstractresourceI absRsc, final InputStream anonIs, File outDir);

}
