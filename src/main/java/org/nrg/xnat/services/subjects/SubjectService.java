package org.nrg.xnat.services.subjects;

import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;

import java.util.List;
import java.util.Optional;

public interface SubjectService {
	
    public Optional<List<XnatSubjectdata>> findAll(UserI user) throws NotFoundException;

    public Optional<XnatSubjectdata> findById(UserI user, String subjectId) throws DataFormatException, NotFoundException;

    public Optional<List<XnatSubjectdata>> findAllByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;

    public Optional<XnatSubjectdata> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId) throws DataFormatException, NotFoundException;

    public XnatSubjectdata create(UserI user, XnatSubjectdata xnatSubjectdata) throws XftItemException, ActionException, Exception;
    
    public XnatSubjectdata update(UserI user, XnatSubjectdata xnatSubjectdata, String label) throws XftItemException, Exception;

    public void deleteById(UserI user, String subjectId) throws ClientException, DataFormatException, NotFoundException;
}
