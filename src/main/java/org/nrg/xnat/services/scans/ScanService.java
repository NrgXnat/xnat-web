package org.nrg.xnat.services.scans;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xft.security.UserI;

public interface ScanService {
	
	public Optional<List<XnatImagescandata>> findAllScanTypesByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;

	public Optional<List<XnatImagescandata>> findAllScanTypes(UserI user) throws NotFoundException;
	
	public Optional<List<XnatImagescandata>> findAllByAssessedId(UserI user, String assessedId) throws DataFormatException, NotFoundException;

	public Optional<XnatImagescandata> findByAssessedIdAndScanId(UserI user, String assessedId, Integer scanId) throws DataFormatException, NotFoundException;

	public Optional<List<XnatImagescandata>> findAllByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId) throws DataFormatException, NotFoundException;

	public Optional<XnatImagescandata> findByProjectIdAndSubjectIdAndExperimentIdAndScanId(UserI user, String projectId, String subjectId, String experimentId, Integer scanId) throws DataFormatException, NotFoundException;

	public void deleteById(UserI user, String assessedId, Integer scanId) throws NotFoundException, DataFormatException, InitializationException;
	
	public void delete(UserI user, XnatImagescandata scan,  String assessedId, Integer scanIds) throws NotFoundException, DataFormatException, InitializationException;
	
}
