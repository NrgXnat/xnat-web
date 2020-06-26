package org.nrg.xnat.export.interfaces;

import java.io.File;

import org.nrg.xdat.om.XnatImagesessiondata;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface ExportLabelMappingI {
	void label(XnatImagesessiondata imageSession, File mappingFile);
}
