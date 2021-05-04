package org.nrg.xnat.services.projects.impl;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.IDAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.projects.ProjectAccessibilityService;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectAccessibilityServiceImpl implements ProjectAccessibilityService {

	@Override
	public Optional<String> findByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException{
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId +" wasn't found");
		}
		
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		
		if(Objects.isNull(project)) {
			throw new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId);
		}
		
		String result = getProjectAccessibility(project);
		if(StringUtils.isBlank(result)) {
			throw new NotFoundException( "An error occurred trying to retrieve the accessibility setting for the project '{}'", project.getId());
		}
    	return Optional.of(result);
	}

	@Override
	public Optional<String> findByProjectIdAndAccessLevel(UserI user, String projectId, String accessLevel) throws NotFoundException, DataFormatException {
		return findByProjectId(user, projectId);
	}

	@Override
	public String update(UserI user, String access, String projectId, XnatEventUtil event) throws NotFoundException, InsufficientPrivilegesException, JustificationAbsent, ActionNameAbsent, IDAbsent  {
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if (StringUtils.isBlank(access) || project == null) 
			throw new NotFoundException( "An error occurred trying to retrieve the accessibility setting for the project '{}'", project.getId());

		try {
			if (!Permissions.canDelete(user, project)) 
				throw new InsufficientPrivilegesException(user.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// If we don't allow non private projects, we shouldn't allow accessibility to change.
		verifyProjectAccessibility(user);
		
		updateProjectAccessibity(access, project, user,event);
		
		return getProjectAccessibility(XnatProjectdata.getXnatProjectdatasById(projectId, user, false));
	}

	private void updateProjectAccessibity(String access, XnatProjectdata project, UserI user, XnatEventUtil event2) throws JustificationAbsent, ActionNameAbsent, IDAbsent  {
		final String currentAccess = getProjectAccessibility(project);
		if (!StringUtils.equals(currentAccess, access)) {
			final PersistentWorkflowI workflow = WorkflowUtils.buildProjectWorkflow(user, project, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ACCESS, EventUtils.MODIFY_PROJECT_ACCESS,event2));
			final EventMetaI event = workflow.buildEvent();
			try {
				if (Permissions.setDefaultAccessibility(project.getId(), access, true, user, event)) {
					WorkflowUtils.complete(workflow, event);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void verifyProjectAccessibility(UserI user) throws InsufficientPrivilegesException {
		final boolean nonPrivateAllowed = XDAT.getBoolSiteConfigurationProperty("securityAllowNonPrivateProjects", true);
		if (!nonPrivateAllowed) {
			log.debug("Unable to change project accessibility because securityAllowNonPrivateProjects is set to" + String.valueOf(nonPrivateAllowed));
			log.debug("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
			throw new InsufficientPrivilegesException(user.getUsername());
		}
	}
	
	private String getProjectAccessibility(XnatProjectdata project)  {
		try {
			return project.getPublicAccessibility();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
