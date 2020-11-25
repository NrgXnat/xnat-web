package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xft.security.UserI;

public interface ResourceService {
	
	public List<XnatAbstractresource> findByExperimentId(final UserI user, final String experimentId);
	
	public XnatAbstractresource findByIdAndExperimentId(final UserI user, final Integer resourceId, final String experimentId);

	public List<XnatAbstractresource> findByProjectAndSubjectAndExperiment(final UserI user, final String projectId, final String subjectId, final String experimentId);
	
	public List<XnatAbstractresource> getResourceByExperimentAndScan(final UserI user, final String assessedId, final String scanId);
	
	public List<XnatAbstractresource> findByProject(final UserI user, final String projectId);

	public XnatAbstractresource findByIdAndProject(final UserI user, final Integer resourceId, final String projectId);

	public List<XnatAbstractresource> findBySubject(final UserI user, final String subjectId);
}
