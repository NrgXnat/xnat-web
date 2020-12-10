package org.nrg.xnat.services.subjects;

import org.nrg.action.ClientException;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;

import java.util.List;

public interface SubjectService {
    List<XnatSubjectdata> getAll(UserI user);

    XnatSubjectdata findById(UserI user, String subjectId);

    List<XnatSubjectdata> findByProject(UserI user, String projectId);

    XnatSubjectdata findByProjectAndSubject(UserI user, String projectId, String subjectId);

    XnatSubjectdata create(UserI user, XnatSubjectdata xnatSubjectdata) throws XftItemException;

    XnatSubjectdata update(UserI user, XnatSubjectdata xnatSubjectdata) throws XftItemException;

    void deleteById(UserI user, String subjectId) throws ClientException;

    void delete(UserI user, XnatSubjectdata subject) throws ClientException;
}
