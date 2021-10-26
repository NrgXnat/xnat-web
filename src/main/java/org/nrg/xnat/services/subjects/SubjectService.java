package org.nrg.xnat.services.subjects;

import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.model.XnatSubjectdataI;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

import java.util.List;
import java.util.Optional;

public interface SubjectService {

    List<XnatSubjectdataI> findAll(UserI user) throws NotFoundException;

    Optional<XnatSubjectdataI> findById(UserI user, String subjectId) throws DataFormatException, NotFoundException;

    List<XnatSubjectdataI> findAllByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;

    Optional<XnatSubjectdataI> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId) throws DataFormatException, NotFoundException;

    XnatSubjectdataI create(UserI user, XnatSubjectdataI xnatSubjectdata, XnatEventUtil event) throws XftItemException, ActionException, Exception;

    XnatSubjectdataI update(UserI user, XnatSubjectdataI xnatSubjectdata, String label, boolean primary, String gender, XnatEventUtil event) throws XftItemException, Exception;

    void deleteById(UserI user, String subjectId, boolean removeFiles, XnatEventUtil event) throws ClientException, DataFormatException, NotFoundException, InitializationException, InsufficientPrivilegesException;
}
