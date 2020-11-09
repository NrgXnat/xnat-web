package org.nrg.xnat.services.scans;

import java.util.List;

import org.nrg.xdat.om.XnatScscandata;
import org.nrg.xft.security.UserI;

public interface ScanService {
	
	List<XnatScscandata> findScanTypesByProject(UserI sessionUser, String projectId);

	List<XnatScscandata> getAllScanTypes(UserI sessionUser);
}
