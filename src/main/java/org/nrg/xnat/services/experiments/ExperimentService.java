package org.nrg.xnat.services.experiments;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xft.security.UserI;

public interface ExperimentService {

	public XnatExperimentdata create(UserI user, XnatExperimentdata xnatExperimentdata,String projectId, String subjectId) throws Exception;

	public Optional<List<XnatExperimentdata>> findAll(UserI user) throws NotFoundException;
	
	public Optional<XnatExperimentdata> findById(UserI user, String experimentId) throws DataFormatException, NotFoundException;
	
	public Optional<List<XnatExperimentdata>> findAllByProjectIdAndSubjectId(UserI user, String projectId, String subject) throws DataFormatException, NotFoundException;

	public Optional<List<XnatExperimentdata>> findAllByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;
	
	public Optional<List<XnatExperimentdata>> findAllByProjectIdAndLabel(UserI user, String projectId, String label);

	public XnatExperimentdata update(UserI user, XnatExperimentdata xnatExperimentdata, String experimentId, String projectId, String subjectId) throws Exception;

	public void deleteById(UserI user, String experimentId, String projectId) throws DataFormatException, NotFoundException, org.nrg.framework.exceptions.NotFoundException ;
	
    void delete(UserI user, XnatExperimentdata xnatExperimentdata,  String projectId) throws DataFormatException, NotFoundException, org.nrg.framework.exceptions.NotFoundException ;

	public Optional<XnatExperimentdata> findByIdAndProjectId(UserI user, String experimentId, String projectId) throws DataFormatException, NotFoundException;
}
