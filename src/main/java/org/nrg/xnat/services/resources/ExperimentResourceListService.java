package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xapi.model.subjects.XnatExperimentResource;
import org.nrg.xft.security.UserI;

public interface ExperimentResourceListService {
	
	public XnatExperimentResource create(UserI user, XnatExperimentResource item);

	public List<XnatExperimentResource> getAll(UserI user);

	public XnatExperimentResource get(UserI user, int itemId);

	public List<XnatExperimentResource> findResourceByExperimentId(UserI user, String itemId);
	
	public List<XnatExperimentResource> findExperimentScanResourcesByAssessedIdAndScanId(UserI user, String assessedId, String scanId);
}
