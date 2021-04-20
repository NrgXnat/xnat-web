package org.nrg.xnat.services.experiments;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xft.security.UserI;

public interface AssessorService {

	public Optional<List<XnatImageassessordata>> findAllByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId) throws DataFormatException, NotFoundException;
	
	public Optional<XnatImageassessordata> findByIdAndProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId, String assessorId) throws DataFormatException, NotFoundException;

	public Optional<List<XnatImageassessordata>> findAllByExperimentId(UserI user, String experimentId) throws DataFormatException, NotFoundException;

	public Optional<XnatImageassessordata> findByIdAndExperimentId(UserI user, String assessorId, String experimentId) throws DataFormatException, NotFoundException;
}
