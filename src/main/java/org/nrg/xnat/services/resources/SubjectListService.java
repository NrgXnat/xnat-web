package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xapi.model.subjects.XnatSubject;
import org.nrg.xft.security.UserI;

public interface SubjectListService {
	
	public XnatSubject create(UserI user, XnatSubject item);

	public List<XnatSubject> getAll(UserI user);

	public XnatSubject get(UserI user, int itemId);

	public XnatSubject findById(UserI user, String itemId);

	public List<XnatSubject> findSubjectsByProjectId(UserI sessionUser, String projectId);

}
