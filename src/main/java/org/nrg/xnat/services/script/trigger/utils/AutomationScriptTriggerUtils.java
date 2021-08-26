package org.nrg.xnat.services.script.trigger.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.framework.constants.Scope;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.XFTTable;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.utils.WorkflowUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutomationScriptTriggerUtils {

	protected static final         String  SITE_SCOPE      = Scope.encode(Scope.Site, "");
	public void intialize(String entityId, String projectId, UserI user ) throws InitializationException, NotFoundException, DataFormatException {
	        final boolean hasEntityId = StringUtils.isNotBlank(entityId);
	        final boolean hasProjectId = StringUtils.isNotBlank(projectId);

	        if (hasEntityId && hasProjectId) {
	            throw new DataFormatException("You can't specify both entity and project ID. Pick one and stick with it.");
	        }

	        if (!hasEntityId && !hasProjectId) {
	            _scope = null;
	            _projectId = null;
	            _hasProjectId = false;
	        } else if (hasEntityId && entityId.equalsIgnoreCase(Scope.Site.code())) {
	            _scope = Scope.Site;
	            _projectId = null;
	            _hasProjectId = false;
	        } else {
	            final Map<String, String> values;
	            if (hasEntityId) {
	                final Map<String, String> entityProps = Scope.decode(entityId);
	                _scope = Scope.getScope(entityProps.get("scope"));
	                values = validateEntityId(entityProps.get("entityId"),user);
	            } else {
	                _scope = Scope.Project;
	                values = validateEntityId(projectId,user);
	            }
	            // For now we presume entity ID is a project ID. This will change soon.
	            if (values != null) {
	                _projectId = values.get(KEY_PROJECTID);
	                _hasProjectId = true;
	            } else {
	                _projectId = null;
	                _hasProjectId = false;
	            }
	        }

	        //_path = request.getResourceRef().getRemainingPart();
	}
	
	protected Scope getScope() {
		return _scope == null ? Scope.Site : _scope;
	}

	protected boolean hasProjectId() {
		return _hasProjectId;
	}

	protected String getProjectId() {
		return _projectId;
	}

	protected void setProjectId(final String projectId) {
		_projectId = projectId;
		_hasProjectId = StringUtils.isNotBlank(_projectId);
	}

	protected String getAssociation() {
		if (getScope() == null) {
			return null;
		}
		return Scope.encode(getScope(), getProjectId());
	}

	protected void setAssociation(final String association) {
		final Map<String, String> atoms = Scope.decode(association);
		_scope = Scope.getScope(atoms.get("scope"));
		_projectId = _scope == Scope.Site ? null : atoms.get("entityId");
	}
	
	protected void recordAutomationEvent(final String automationId, final String containerId, final String operation, final Class<?> type, UserI user) {
        try {
            final EventDetails instance = EventUtils.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.TYPE.WEB_SERVICE, operation, "", operation + " " + type + " with ID " + automationId);
            PersistentWorkflowI workflow = PersistentWorkflowUtils.buildOpenWorkflow(user, type.getName(), automationId, containerId, instance);
            assert workflow != null;
            workflow.setStatus(PersistentWorkflowUtils.COMPLETE);
            WorkflowUtils.save(workflow, workflow.buildEvent());
        } catch (PersistentWorkflowUtils.ActionNameAbsent | PersistentWorkflowUtils.IDAbsent | PersistentWorkflowUtils.JustificationAbsent exception) {
            // This is not really going to happen because we're providing all the attributes required, but we still have to handle it.
            log.warn("An error occurred trying to save a workflow when working with event", exception);
        } catch (Exception exception) {
            log.error("An error occurred trying to save a workflow when working with event", exception);
        }
    }

	protected void validateProjectAccess(final String projectId, UserI user) throws NotFoundException, InitializationException {
		final XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if (project == null) {
			throw new NotFoundException("Can't find project with ID: " + getProjectId());
		}
		try {
			if ((!project.canEdit(user)) || !project.canRead(user)) {
				final String message = "User " + user.getLogin() + " attempted to access project " + getProjectId() + " with insufficient privileges.";
				log.warn(message);
				throw new InsufficientPrivilegesException(message);
			}
		} catch (Exception e) {
			throw new InitializationException(
					"Something went wrong accessing project info for " + getProjectId());
		}
	}
	 protected Map<String, String> validateEntityId(final String entityId, UserI user) throws InitializationException, NotFoundException, DataFormatException  {
	        if (getScope() == null) {
	            return null;
	        }

	        switch (getScope()) {
	            case Site:
	                return null;

	            case Project:
	                if (StringUtils.isBlank(entityId)) {
	                    throw new NotFoundException( "You must specify an ID for the project scope.");
	                }
	                final Map<String, String> ids = new HashMap<>();
	                // Check to see if entityId is actually a project ID. If so, convert it to a Long.
	                final Long resolved = XnatProjectdata.getProjectInfoIdFromStringId(entityId);
	                if (resolved != null) {
	                    ids.put(KEY_PROJECTDATAINFO, resolved.toString());
	                    ids.put(KEY_PROJECTID, entityId);
	                } else {
	                    if (!NumberUtils.isParsable(entityId)) {
	                        throw new NotFoundException( "The specified entity ID " + entityId + " does not match an existing project ID, but is also not a parseable number, so is not project data ID.");
	                    }
	                    try {
	                        XFTTable table = XFTTable.Execute("SELECT id FROM xnat_projectdata WHERE projectdata_info = " + entityId, user.getDBName(), user.getUsername());
	                        if (table.size() != 1) {
	                            throw new NotFoundException( "Couldn't find a project with the ID or alias of " + entityId);
	                        }
	                        ids.put(KEY_PROJECTDATAINFO, entityId);
	                        ids.put(KEY_PROJECTID, (String) table.convertColumnToArrayList("id").get(0));
	                    } catch (Exception e) {
	                        throw new InitializationException("An error occurred trying to access the database.", e);
	                    }
	                }
	                return ids;

	            default:
	                throw new DataFormatException( "The specified scope " + getScope().code() + " is not currently supported. Supported scopes include: " + Scope.Site.code() + " and " + Scope.Project.code());
	        }
	    }
	
	private static final String ENTITY_ID = "ENTITY_ID";
	private static final String PROJECT_ID = "PROJECT_ID";
	private static final String KEY_PROJECTDATAINFO = "projectDataInfo";
	private static final String KEY_PROJECTID = "projectId";

	private String _path;
	protected Scope _scope;
	protected boolean _hasProjectId;
	protected String _projectId;
	
}
