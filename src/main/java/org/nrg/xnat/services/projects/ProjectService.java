package org.nrg.xnat.services.projects;

import org.nrg.action.ActionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
	
     List<XnatProjectdata> findAll(UserI user) throws NotFoundException;

     Optional<XnatProjectdata> findById(UserI user, String projectId) throws DataFormatException, NotFoundException;

     XnatProjectdata create(UserI user, XnatProjectdata xnatProjectdata, String allowDataDelete, String accessibility,  String xsiType, XnatEventUtil event) throws XftItemException, ActionException, UserNotFoundException, UserInitException, DataFormatException, InsufficientPrivilegesException, ResourceAlreadyExistsException;

     XnatProjectdata update(UserI user, XnatProjectdata xnatProjectdata,  String filepath, String allowDataDelete, String accessibility, Boolean testHyphen,  String xsiType, XnatEventUtil event) throws InsufficientPrivilegesException, DataFormatException, InitializationException, Exception;

     void deleteById(UserI user, String projectId,  boolean removeFiles, XnatEventUtil event) throws DataFormatException, InitializationException, NotFoundException;
}
