package org.nrg.xnat.services.projects;

import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;

import java.util.List;

public interface ProjectService {
    List<XnatProjectdata> getAll(UserI user);

    XnatProjectdata findById(UserI user, String projectId);

    XnatProjectdata create(UserI user, XnatProjectdata xnatProjectdata) throws XftItemException;

    XnatProjectdata update(UserI user, XnatProjectdata xnatProjectdata);

    void deleteById(UserI user, String projectId);

    void delete(UserI user, XnatProjectdata project);
}
