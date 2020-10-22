package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xapi.model.subjects.XnatScan;
import org.nrg.xft.security.UserI;

public interface ScanListService extends XftDataObjectService<XnatScan> {

	public List<XnatScan> findScansByExperimentId(UserI user, String experimentId);
}