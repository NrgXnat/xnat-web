package org.nrg.xnat.export.interfaces;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.exception.FailedToExportException;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.manifest.TransportManifest;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface ExporterI {
	
	void export(ExportManifest exportManifest, UserI user) throws FailedToExportException; 
	
	TransportManifest dryrun(ExportManifest exportManifest, UserI user);
}
