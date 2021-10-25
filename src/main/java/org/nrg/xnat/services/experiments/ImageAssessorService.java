package org.nrg.xnat.services.experiments;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xft.security.UserI;

public interface ImageAssessorService {

	  List<XnatImageassessordataI> findAllByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId) throws DataFormatException, NotFoundException;
	
	 Optional<XnatImageassessordataI> findByIdAndProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId, String assessorId) throws DataFormatException, NotFoundException;

	 List<XnatImageassessordataI> findAllByExperimentId(UserI user, String experimentId) throws DataFormatException, NotFoundException;

	 Optional<XnatImageassessordataI> findByIdAndExperimentId(UserI user, String assessorId, String experimentId) throws DataFormatException, NotFoundException;
}
