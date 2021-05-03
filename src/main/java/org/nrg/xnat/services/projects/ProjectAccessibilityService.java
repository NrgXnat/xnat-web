package org.nrg.xnat.services.projects;

import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.IDAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface ProjectAccessibilityService {
	
	 Optional<String> findByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException;
	
	 Optional<String> findByProjectIdAndAccessLevel(UserI user, String projectId, String accessLevel) throws NotFoundException, DataFormatException;
	
	 String update(UserI user, String access, String projectId,XnatEventUtil event) throws NotFoundException, InsufficientPrivilegesException, JustificationAbsent, ActionNameAbsent, IDAbsent;
}
