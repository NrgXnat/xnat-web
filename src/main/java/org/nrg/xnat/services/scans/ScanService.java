package org.nrg.xnat.services.scans;

import java.util.List;

import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xft.security.UserI;

public interface ScanService {
	
	public List<XnatImagescandata> findScanTypesByProject(UserI user, String projectId);

	public List<XnatImagescandata> getAllScanTypes(UserI user);
	
	public List<XnatImagescandata> findByAssessed(UserI user, String assessedId);

	public XnatImagescandata findByAssessedAndScan(UserI user, String assessedId, String scanId);

	public List<XnatImagescandata> findByProjectAndSubjectAndExperiment(UserI user, String projectId, String subjectId, String experimentId);

	public XnatImagescandata findByProjectAndSubjectAndExperimentAndScan(UserI user, String projectId, String subjectId, String experimentId, String scanId);
	
}
