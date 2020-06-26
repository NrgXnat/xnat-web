package org.nrg.xnat.export.jms.requests;

import java.io.Serializable;

import org.nrg.xnat.export.manifest.ExportManifest;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6098797436217911534L;
	public static final String destination = "exportRequest";

	ExportManifest _exportManifest;

	
	public ExportRequest(ExportManifest exportManifest) {
		_exportManifest = exportManifest;
	}


	/**
	 * @return the _exportManifest
	 */
	public ExportManifest get_exportManifest() {
		return _exportManifest;
	}


	/**
	 * @param _exportManifest the _exportManifest to set
	 */
	public void set_exportManifest(ExportManifest _exportManifest) {
		this._exportManifest = _exportManifest;
	}
	
	public String getDestination() {
		return destination;
	}

}
