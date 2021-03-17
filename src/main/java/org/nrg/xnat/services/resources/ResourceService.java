package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;

public interface ResourceService {
	
	public List<XnatAbstractresource> findByExperimentId(final UserI user, final String experimentId);
	
	public XnatAbstractresource findByIdAndExperimentId(final UserI user, final Integer resourceId, final String experimentId);

	public List<XnatAbstractresource> findByProjectAndSubjectAndExperiment(final UserI user, final String projectId, final String subjectId, final String experimentId);
	
	public List<XnatAbstractresource> findResourceByExperimentAndScan(final UserI user, final String assessedId, final String scanId) throws Exception;
	
	public List<XnatAbstractresource> findByProject(final UserI user, final String projectId);
	
	public List<XnatAbstractresource> findByProjectAndLabel(final UserI user, final String projectId, String label);

	public XnatAbstractresource findByIdAndProject(final UserI user, final Integer resourceId, final String projectId);

	public List<XnatAbstractresource> findBySubject(final UserI user, final String subjectId);
	
    public List<XnatAbstractresource> findByProjectAndSubject(final UserI user, final String projectId, final String subjectId);
	
	public XnatAbstractresource findByIdAndProjectAndSubject(final UserI user,final Integer resourceId, final String projectId, final String subjectId);

	public XnatAbstractresource findByIdAndSubject(UserI sessionUser, Integer resourceId, String subjectId);

	public List<XnatAbstractresource> findResourceByexperimentIdAndAssessedId(UserI user,String experimentId, String assessedId, String type);

	public XnatAbstractresource findResourceByexperimentIdAndAssessedIdAndResourceId(UserI sessionUser, String experimentId, String assessedId, String type, Integer resourceId);

	public List<XnatAbstractresource> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId, String type);

	public XnatResourcecatalog create(UserI user, String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type, XnatResource xnatResourcecatalog);

	public void deleteByProjectIdAndResourceId(UserI user, String projectId, String subjectId, String experimentId,String assessorId,String scanId,String type, String resourceId);

	public List<XnatAbstractresource> findByProjectAndSubjectAndExperimentAndScans(UserI user, String projectId, String subjectId, String assessedId, String scanId);
}