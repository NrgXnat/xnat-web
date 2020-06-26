package org.nrg.xnat.export.interfaces;

import org.nrg.xnat.export.manifest.TransportManifest;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface HTTPExportI {
	void transport(TransportManifest transportManifest);
}
