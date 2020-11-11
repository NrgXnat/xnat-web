package org.nrg.xnat.services.scans;

import java.util.List;

import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xft.security.UserI;

public interface ScanService {
	
	public List<XnatImagescandata> findScanTypesByProject(UserI sessionUser, String projectId);

	public List<XnatImagescandata> getAllScanTypes(UserI sessionUser);
	
	public List<XnatImagescandata> findByExperiments(UserI sessionUser, String experimentId);
	
}
