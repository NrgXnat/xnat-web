package org.nrg.xnat.services.scans;

import java.util.List;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;

public interface ScanService {
	
	public List<XnatImagescandata> findScanTypesByProject(UserI user, String projectId);

	public List<XnatImagescandata> getAllScanTypes(UserI user);
	
	public List<XnatImagescandata> findByAssessed(UserI user, String assessedId);

	public XnatImagescandata findByAssessedAndScan(UserI user, String assessedId, Integer scanId);

	public List<XnatImagescandata> findByProjectAndSubjectAndExperiment(UserI user, String projectId, String subjectId, String experimentId);

	public XnatImagescandata findByProjectAndSubjectAndExperimentAndScan(UserI user, String projectId, String subjectId, String experimentId, String scanId);

	public XnatImagescandata create(UserI user, String projectId, String subjectId, String assessedId, XnatImagescandata scan) throws NotFoundException;

	public void deleteById(UserI user, String assessedId, Integer scanId) throws NotFoundException, DataFormatException, InitializationException;
	
	 void delete(UserI user, XnatImagescandata scan,  String assessedId, Integer scanIds) throws NotFoundException, DataFormatException, InitializationException;
	
}
