package org.nrg.xnat.services.subjects;

import java.util.List;

import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;

public interface SubjectService {

	public List<XnatSubjectdata> getAll(UserI user);
	
	public XnatSubjectdata findById(UserI user, String subjectId);
	
	public List<XnatSubjectdata> findByProject(UserI user, String projectId);
	
	public XnatSubjectdata findByProjectAndSubject(UserI user, String projectId, String subjectId);
	
	public XnatSubjectdata create(UserI user, XnatSubjectdata xnatSubjectdata);
	
	public XnatSubjectdata update(UserI user, XnatSubjectdata xnatSubjectdata, String subjectId);

	public void deleteById(UserI user, String subjectId);
	
}
