package org.nrg.xnat.services.projects;

import org.nrg.action.ActionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;

import java.util.List;

public interface ProjectService {
    List<XnatProjectdata> getAll(UserI user);

    XnatProjectdata findById(UserI user, String projectId);

    XnatProjectdata create(UserI user, XnatProjectdata xnatProjectdata) throws XftItemException, ActionException, UserNotFoundException, UserInitException, DataFormatException, InsufficientPrivilegesException, ResourceAlreadyExistsException;

    XnatProjectdata update(UserI user, XnatProjectdata xnatProjectdata) throws InsufficientPrivilegesException, DataFormatException, InitializationException, Exception;

    void deleteById(UserI user, String projectId) throws DataFormatException, InitializationException;

    void delete(UserI user, XnatProjectdata project) throws DataFormatException, InitializationException;
}
