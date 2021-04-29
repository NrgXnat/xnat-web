package org.nrg.xnat.services.experiments;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface ExperimentService {
	
	public XnatExperimentdata create(UserI user, XnatExperimentdata xnatExperimentdata,String projectId, String subjectId, String xsiType, String  allowDataDelete, XnatEventUtil event) throws NotFoundException;

	public Optional<List<XnatExperimentdata>> findAll(UserI user) throws NotFoundException;
	
	public Optional<XnatExperimentdata> findById(UserI user, String experimentId) throws DataFormatException, NotFoundException;
	
	public Optional<List<XnatExperimentdata>> findAllByProjectIdAndSubjectId(UserI user, String projectId, String subject) throws DataFormatException, NotFoundException;

	public Optional<List<XnatExperimentdata>> findAllByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;
	
	public Optional<List<XnatExperimentdata>> findAllByProjectIdAndLabel(UserI user, String projectId, String label);

	public XnatExperimentdata update(UserI user, XnatExperimentdata xnatExperimentdata, String experimentId, String projectId, String subjectId, String allowDataDelete, String label, String primary, String moveAssessors, boolean overwrite, String filepath, XnatEventUtil event);

	public void deleteById(UserI user, String experimentId, String projectId, String filepath,boolean removeFiles,XnatEventUtil event) throws DataFormatException, NotFoundException, org.nrg.framework.exceptions.NotFoundException ;
	
	public Optional<XnatExperimentdata> findByIdAndProjectId(UserI user, String experimentId, String projectId) throws DataFormatException, NotFoundException;
}
