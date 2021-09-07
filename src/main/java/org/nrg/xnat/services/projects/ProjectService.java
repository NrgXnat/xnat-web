package org.nrg.xnat.services.projects;

import org.nrg.action.ActionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.IDAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
	
     List<XnatProjectdata> findAll(UserI user) throws NotFoundException;

     Optional<XnatProjectdata> findById(UserI user, String projectId) throws DataFormatException, NotFoundException;

     XnatProjectdata create(UserI user, XnatProjectdata xnatProjectdata, boolean allowDataDeletion, String accessibility,  String xsiType, XnatEventUtil event) throws XftItemException, ActionException, UserNotFoundException, UserInitException, DataFormatException, InsufficientPrivilegesException, ResourceAlreadyExistsException;

     XnatProjectdata update(UserI user, XnatProjectdata xnatProjectdata,  String filepath, boolean allowDataDeletion, String accessibility, Boolean testHyphen,  String xsiType, XnatEventUtil event) throws InsufficientPrivilegesException, DataFormatException, InitializationException, Exception;

     void deleteById(UserI user, String projectId,  boolean removeFiles, XnatEventUtil event) throws DataFormatException, InitializationException, NotFoundException;

     //Project Accessibility service
     
     Optional<String> findByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException;
 	
	 Optional<String> findByProjectIdAndAccessLevel(UserI user, String projectId, String accessLevel) throws NotFoundException, DataFormatException;
	
	 String update(UserI user, String access, String projectId,XnatEventUtil event) throws NotFoundException, InsufficientPrivilegesException, JustificationAbsent, ActionNameAbsent, IDAbsent;

	//Project Archive service
	 Optional<ArcProject> findArcProjectByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException;
}
