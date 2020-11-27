package org.nrg.xnat.services.experiments;

import java.util.List;

import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xft.security.UserI;

public interface AssessorService {

	List<XnatImageassessordata> findByProjectAndSubjectAndExperiment(UserI user, String projectId, String subjectId, String experimentId);
	
	XnatImageassessordata findByIdAndProjectAndSubjectAndExperiment(UserI user, String projectId, String subjectId, String experimentId, String assessorId);
}
