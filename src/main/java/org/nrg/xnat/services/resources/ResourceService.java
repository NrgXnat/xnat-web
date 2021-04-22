package org.nrg.xnat.services.resources;

import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;

public interface ResourceService {
	
	public Optional<List<XnatAbstractresource>> findByExperimentId(final UserI user, final String experimentId) throws NotFoundException, DataFormatException;
	
	public Optional<XnatAbstractresource> findByIdAndExperimentId(final UserI user, final Integer resourceId, final String experimentId) throws DataFormatException, NotFoundException;

	public Optional<List<XnatAbstractresource>> findByProjectIdAndSubjectIdAndExperimentId(final UserI user, final String projectId, final String subjectId, final String experimentId) throws DataFormatException, NotFoundException;
	
	public Optional<List<XnatAbstractresource>> findByExperimentIdAndScanId(final UserI user, final String assessedId, final String scanId) throws DataFormatException, NotFoundException;
	
	public Optional<List<XnatAbstractresource>> findByProjectId(final UserI user, final String projectId) throws DataFormatException, NotFoundException;
	
	public Optional<List<XnatAbstractresource>> findByProjectIdAndLabel(final UserI user, final String projectId, String label) throws DataFormatException, NotFoundException;

	public Optional<XnatAbstractresource> findByIdAndProjectId(final UserI user, final Integer resourceId, final String projectId) throws DataFormatException, NotFoundException;

	public Optional<List<XnatAbstractresource>> findBySubjectId(final UserI user, final String subjectId) throws DataFormatException, NotFoundException;
	
    public Optional<List<XnatAbstractresource>> findByProjectIdAndSubjectId(final UserI user, final String projectId, final String subjectId) throws DataFormatException, NotFoundException;
	
	public Optional<XnatAbstractresource> findByIdAndProjectIdAndSubjectId(final UserI user,final Integer resourceId, final String projectId, final String subjectId) throws DataFormatException, NotFoundException;

	public Optional<XnatAbstractresource> findByIdAndSubjectId(UserI sessionUser, Integer resourceId, String subjectId) throws DataFormatException, NotFoundException;

	public Optional<List<XnatAbstractresource>> findByExperimentIdAndAssessedId(UserI user,String experimentId, String assessedId, String type) throws DataFormatException, NotFoundException;

	public Optional<XnatAbstractresource> findByExperimentIdAndAssessedIdAndResourceId(UserI sessionUser, String experimentId, String assessedId, String type, Integer resourceId) throws DataFormatException, NotFoundException;

	public Optional<List<XnatAbstractresource>> findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId, String type) throws DataFormatException, NotFoundException;

	public XnatResourcecatalog create(UserI user, String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type, XnatResource xnatResourcecatalog);

	public void deleteByProjectIdAndResourceId(UserI user, String projectId, String subjectId, String experimentId,String assessorId,String scanId,String type, String resourceId);

	public Optional<List<XnatAbstractresource>> findByProjectIdAndSubjectIdAndExperimentIdAndScanId(UserI user, String projectId, String subjectId, String assessedId, String scanId) throws DataFormatException, NotFoundException;
}