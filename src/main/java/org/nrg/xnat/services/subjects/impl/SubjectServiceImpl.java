package org.nrg.xnat.services.subjects.impl;

import java.util.List;

import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.subjects.SubjectService;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl implements SubjectService {

	@Override
	public List<XnatSubjectdata> getAll(UserI user) {
		return XnatSubjectdata.getAllXnatSubjectdatas(user, false);

	}

	@Override
	public XnatSubjectdata findById(UserI user, String subjectId) {
		return XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
	}
	
	@Override
	public List<XnatSubjectdata> findByProjectAndSubject(UserI user, String projectId, String subjectId) {
		return null;
	}

	@Override
	public List<XnatSubjectdata> findByProject(UserI user, String projectId) {
		return null;
	}

	@Override
	public XnatSubjectdata create(UserI user, XnatSubjectdata xnatSubjectdata) {
		return null;
	}

	@Override
	public XnatSubjectdata update(UserI user, XnatSubjectdata xnatSubjectdata, String subjectId) {
		return null;
	}

	@Override
	public void deleteById(UserI user, String subjectId) {

	}

}
