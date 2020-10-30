package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xft.security.UserI;

public interface ExperimentService {

	public XnatExperimentdata create(UserI user, XnatExperimentdata XnatExperimentdata);

	public List<XnatExperimentdata> getAll(UserI user);
	
	public List<XnatExperimentdata> getAllExperiments(UserI user);

	public XnatExperimentdata findById(UserI user, String experimentId);
	
	public XnatExperimentdata findByExperimentId(UserI user, String experimentId);

	public List<XnatExperimentdata> findByProject(UserI user, String projectId);
	
	public List<XnatExperimentdata> findByLabel(UserI user, String label);

	public XnatExperimentdata update(UserI user, XnatExperimentdata XnatExperimentdata, String experimentId);

	public void deleteById(UserI user, String experimentId);
}
