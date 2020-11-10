package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xft.security.UserI;

public interface ResourceService {
	
	List<XnatAbstractresource> findByExperimentId(final UserI user, final String experimentId);
	
	XnatAbstractresource findByIdAndExperimentId(final UserI user, final Integer resourceId, final String experimentId);

	List<XnatAbstractresource> findByProjectAndSubjectAndExperiment(UserI sessionUser, String projectId, String subjectId, String experimentId);

}
