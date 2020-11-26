package org.nrg.xnat.services.experiments;

import java.util.List;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xft.security.UserI;

public interface ExperimentService {

	public XnatExperimentdata create(UserI user, XnatExperimentdata xnatExperimentdata);

	public List<XnatExperimentdata> getAll(UserI user);
	
	public XnatExperimentdata findById(UserI user, String experimentId);
	
	public List<XnatExperimentdata> findByProjectAndSubject(UserI user, String projectId, String subject);

	public List<XnatExperimentdata> findByProject(UserI user, String projectId);
	
	public List<XnatExperimentdata> findByProjectAndLabel(UserI user, String projectId, String label);

	public XnatExperimentdata update(UserI user, XnatExperimentdata xnatExperimentdata, String experimentId);

	public void deleteById(UserI user, String experimentId);

	public XnatExperimentdata findByIdAndProject(UserI user, String experimentId, String projectId);
}
