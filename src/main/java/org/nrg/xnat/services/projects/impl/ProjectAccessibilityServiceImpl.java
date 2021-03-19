package org.nrg.xnat.services.projects.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.projects.ProjectAccessibilityService;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectAccessibilityServiceImpl implements ProjectAccessibilityService {

	@Override
	public String findByProjectId(UserI user, String projectId) throws Exception {
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if (Objects.nonNull(project))
			return getProjectAccessibility(project);
		else
			throw new NotFoundException( "An error occurred trying to retrieve the accessibility setting for the project '{}'", project.getId());
	}

	private String getProjectAccessibility(XnatProjectdata project) throws Exception {
		return project.getPublicAccessibility();
	}
	
	@Override
	public String findByProjectIdAndAccessLevel(UserI user, String projectId, String accessLevel) throws NotFoundException, Exception {
		return findByProjectId(user, projectId);
	}

	@Override
	public String update(UserI user, String access, String projectId) throws Exception {
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if (StringUtils.isBlank(access) || project == null) 
			throw new NotFoundException( "An error occurred trying to retrieve the accessibility setting for the project '{}'", project.getId());

		if (!Permissions.canDelete(user, project)) 
			throw new InsufficientPrivilegesException(user.getUsername());

		// If we don't allow non private projects, we shouldn't allow accessibility to change.
		verifyProjectAccessibility(user);
		
		updateProjectAccessibity(access, project, user);
		
		return getProjectAccessibility(XnatProjectdata.getXnatProjectdatasById(projectId, user, false));
	}

	private void updateProjectAccessibity(String access, XnatProjectdata project, UserI user) throws Exception {
		final String currentAccess = getProjectAccessibility(project);
		if (!StringUtils.equals(currentAccess, access)) {
			final PersistentWorkflowI workflow = WorkflowUtils.buildProjectWorkflow(user, project, newEventInstance(EventUtils.CATEGORY.PROJECT_ACCESS, EventUtils.MODIFY_PROJECT_ACCESS));
			final EventMetaI event = workflow.buildEvent();
			if (Permissions.setDefaultAccessibility(project.getId(), access, true, user, event)) {
				WorkflowUtils.complete(workflow, event);
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

	public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
		return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action,
				getReason(), getComment());
	}

	public String getAction() {
		return getQueryVariable(EventUtils.EVENT_ACTION);
	}

	public String getComment() {
		return getQueryVariable(EventUtils.EVENT_COMMENT);
	}

	public String getReason() {
		return getQueryVariable(EventUtils.EVENT_REASON);
	}

	public EventUtils.TYPE getEventType() {
		final String id = getQueryVariable(EventUtils.EVENT_TYPE);
		if (id != null) {
			return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
		} else {
			return EventUtils.TYPE.WEB_SERVICE;
		}
	}

	private String getQueryVariable(String eventType) {
		return null;
	}

}
