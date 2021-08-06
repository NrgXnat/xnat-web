package org.nrg.xnat.services.script.trigger.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.nrg.automation.entities.ScriptTrigger;
import org.nrg.automation.services.ScriptTriggerService;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.constants.Scope;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.event.util.ImportEventHandlerResults;
import org.nrg.xnat.event.util.JsonResults;
import org.nrg.xnat.services.script.trigger.AutoHandlerScriptTriggerService;
import org.nrg.xnat.services.script.trigger.dto.ScriptTriggerDto;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class AutoHandlerScriptTriggerServiceImpl<T> implements AutoHandlerScriptTriggerService {

	@Autowired
	public AutoHandlerScriptTriggerServiceImpl(final ScriptTriggerService scriptTriggerService) {
		this._scriptTriggerService = scriptTriggerService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T findScriptTrigger(UserI user, String entityId, String projectId, String triggerId, String eventId, String id) throws NotFoundException, InitializationException, InsufficientPrivilegesException {
		final boolean hasEntityId = StringUtils.isNotBlank(entityId);
        final boolean hasProjectId = StringUtils.isNotBlank(projectId);
        
        setProjectId(projectId);
        
        validateScop(hasEntityId, hasProjectId, entityId, projectId);

		final boolean hasEvent = StringUtils.isNotBlank(eventId);
		final boolean hasTriggerId = StringUtils.isNotBlank(triggerId);
		final boolean hasId = StringUtils.isNotBlank(id);
		if (!hasTriggerId && !hasEvent && !hasId) {
			_trigger = null;
		}else {
			if (hasId || hasTriggerId) {
				_trigger =  getTrigger(hasId, id, triggerId);
				projectId = getProjectIdWithAssociation(projectId);
			}else if (_hasProjectId) {
				_trigger = null;
			}else {
				projectId = null;
				_trigger = null;
			}
		}
		
		validateProjectAndTrigger(projectId, eventId, user, hasEvent);
        
        //Need to add IMPL
		ScriptTrigger trigger = getScriptTrigger();
		
		if (trigger != null) {
			return (T) trigger;
		}else {
			return (T) listScriptTriggers(projectId);
		}
	}
	
	@Override
	public void update(UserI user, String projectId, String triggerId,String eventId,String id,ImportEventHandlerResults results) throws DataFormatException, ConfigServiceException, NotFoundException, InitializationException, InsufficientPrivilegesException {
		setProjectId(projectId);
		final boolean hasEvent = StringUtils.isNotBlank(eventId);
		final boolean hasTriggerId = StringUtils.isNotBlank(triggerId);
		final boolean hasId = StringUtils.isNotBlank(id);
		if (!hasTriggerId && !hasEvent && !hasId) {
			_trigger = null;
		}else {
			if (hasId || hasTriggerId) {
				_trigger =  getTrigger(hasId, id, triggerId);
				projectId = getProjectIdWithAssociation(projectId);
			}else if (_hasProjectId) {
				_trigger = null;
			}else {
				projectId = null;
				_trigger = null;
			}
		}
		validateProjectAndTrigger(projectId, eventId, user, hasEvent);
		
		
		Map<String, String> triggerIdMap = new HashMap<String, String>();
		for (int i = 0; i < results.getEventHandlers().length; i++) {
			addEventHandler(results.getEventHandlers()[i], triggerIdMap, projectId,user);
		}
		if (results.getSourceProjectId() != null) {
			ConfigService configService = XDAT.getConfigService();

			String currentConfigJson = configService.getConfigContents(TOOL_NAME_AUTOMATION_UPLOADER,PATH_CONFIGURATION, Scope.Project, getProjectId(projectId));
			JSONArray currentConfig = null;
			if (currentConfigJson != null)
				currentConfig = new JSONArray(currentConfigJson);
			else
				currentConfig = new JSONArray();

			String sourceConfingJson = configService.getConfigContents(TOOL_NAME_AUTOMATION_UPLOADER, PATH_CONFIGURATION, getScope(), results.getSourceProjectId());
			JSONArray srcConfig = null;
			if (sourceConfingJson != null)
				srcConfig = new JSONArray(sourceConfingJson);
			if (srcConfig != null) {
				for (Iterator<String> iterator = triggerIdMap.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					for (int i = 0; i < srcConfig.length(); i++) {
						if (key.equals(srcConfig.getJSONObject(i).getString(EVENT))) {
							srcConfig.getJSONObject(i).put(EVENT_TRIGGER_ID, triggerIdMap.get(key));
							currentConfig.put(srcConfig.getJSONObject(i));
						}
					}
				}
				configService.replaceConfig(user.getLogin(), null, TOOL_NAME_AUTOMATION_UPLOADER, PATH_CONFIGURATION, currentConfig.toString(), getScope(), getProjectId(projectId));
			}
		}
	}
	
	@Override
	public void delete(UserI user, String eventId, String projectId,String triggerId, String id) throws NotFoundException, InitializationException, InsufficientPrivilegesException {
		 	setProjectId(projectId);
			final boolean hasEvent = StringUtils.isNotBlank(eventId);
			final boolean hasTriggerId = StringUtils.isNotBlank(triggerId);
			final boolean hasId = StringUtils.isNotBlank(id);
			if (!hasTriggerId && !hasEvent && !hasId) {
				_trigger = null;
			}else {
				if (hasId || hasTriggerId) {
					_trigger =  getTrigger(hasId, id, triggerId);
					projectId = getProjectIdWithAssociation(projectId);
				}else if (_hasProjectId) {
					_trigger = null;
				}else {
					projectId = null;
					_trigger = null;
				}
			}
			validateProjectAndTrigger(projectId, eventId, user, hasEvent);
			
			if (log.isDebugEnabled()) {
				log.debug("Preparing to delete script trigger for " + formatScopeEntityIdAndEvent(projectId, eventId) + " and its associated triggers.");
			}
			triggerId = _trigger.getTriggerId();
			final String containerId = _trigger.getAssociation();
			_scriptTriggerService.delete(_trigger);
			recordAutomationEvent(triggerId, containerId, "Delete", ScriptTrigger.class, user);
	}
	
	private void addEventHandler(JsonResults jsonResults, Map<String, String> triggerIdMap, String projectId,UserI user) throws DataFormatException {
		if (_trigger == null) {
			if (jsonResults.getEvent() == null || jsonResults.getEvent().length() < 1) {
				throw new DataFormatException("You must specify the event for your new script trigger.");
			}
			if (jsonResults.getScriptId() == null || jsonResults.getScriptId().length() < 1) {
				throw new DataFormatException("You must specify the script ID for your new script trigger.");
			}
			if (log.isDebugEnabled()) {
				log.debug("Creating new script trigger");
			}
			final String scriptId = jsonResults.getScriptId();
			final String event = jsonResults.getEvent();
			final String description = jsonResults.getDescription();
			final String eventClass = jsonResults.getEventClass();
			final Map<String, List<String>> eventFilters = jsonResults.getFilters();
			final String triggerId = _scriptTriggerService.getDefaultTriggerName(scriptId, getScope(), getProjectId(projectId),
					eventClass, event, eventFilters);
			final ScriptTrigger trigger = _scriptTriggerService.newEntity(triggerId, description, scriptId,
					getAssociation(projectId), eventClass, event, eventFilters);
			if (log.isInfoEnabled()) {
				log.info("Created a new trigger: " + trigger.toString());
			}
			recordAutomationEvent(triggerId, getAssociation(projectId), "Create", ScriptTrigger.class, user);
			// Return the trigger ID in the response test. The upload UI needs it
			//this.getResponse().setEntity(new StringRepresentation(triggerId));
			triggerIdMap.put(event, triggerId);
		} else {
			final String scriptId = jsonResults.getScriptId();
			final String event = jsonResults.getEvent();
			final String description = jsonResults.getDescription();
			final String eventClass = jsonResults.getEventClass();
			final Map<String, List<String>> eventFilters = jsonResults.getFilters();
			final String triggerId = _scriptTriggerService.getDefaultTriggerName(scriptId, getScope(), getProjectId(projectId),
					eventClass, event, eventFilters);
			boolean isDirty = false;
			if (StringUtils.isNotBlank(scriptId) && !scriptId.equals(_trigger.getScriptId())) {
				_trigger.setScriptId(scriptId);
				isDirty = true;
			}
			if (StringUtils.isNotBlank(event) && !event.equals(_trigger.getEvent())) {
				_trigger.setEvent(event);
				isDirty = true;
			}
			if (StringUtils.isNotBlank(triggerId) && !triggerId.equals(_trigger.getTriggerId())) {
				_trigger.setTriggerId(triggerId);
				isDirty = true;
			}
			if (StringUtils.isNotBlank(eventClass) && !eventClass.equals(_trigger.getSrcEventClass())) {
				_trigger.setSrcEventClass(eventClass);
				isDirty = true;
			}
			if (eventFilters != null && !eventFilters.equals(_trigger.getEventFiltersAsMap())) {
				_trigger.setEventFiltersAsMap(eventFilters);
				isDirty = true;
			}
			// Description is a little different because you could specify an empty
			// description.
			if (description != null && !description.equals(_trigger.getDescription())) {
				_trigger.setDescription(description);
				isDirty = true;
			}
			if (!getAssociation(projectId).equals(getAssociation(projectId))) {
				_trigger.setAssociation(getAssociation(projectId));
				isDirty = true;
			}
			if (isDirty) {
				_scriptTriggerService.update(_trigger);
				recordAutomationEvent(triggerId, getAssociation(projectId), "Update", ScriptTrigger.class,user);
				// Return thie trigger ID in the response test. The upload UI needs it
				//this.getResponse().setEntity(new StringRepresentation(triggerId));
			}
			triggerIdMap.put(event, triggerId);
		}
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
	
	 protected String getAssociation(String projectId) {
	        if (getScope() == null) {
	            return null;
	        }
	        return Scope.encode(getScope(), getProjectId(projectId));
	    }
	    protected void setAssociation(final String association) {
	        final Map<String, String> atoms = Scope.decode(association);
	        _scope = Scope.getScope(atoms.get("scope"));
	        _projectId = _scope == Scope.Site ? null : atoms.get("entityId");
	    }
	
	
	
	private void validateProjectAndTrigger(String projectId, String eventId, UserI user, boolean hasEvent) throws NotFoundException, InitializationException, InsufficientPrivilegesException {
		if (StringUtils.isNotBlank(projectId)) {
			validateProjectAccess(projectId, user);
			setProjectId(projectId);
		} else if (!Roles.isSiteAdmin(user)) {
			final String message = "User " + user.getLogin() + " attempted to access forbidden script trigger resource at the site level.";
			log.warn(message);
			throw new InsufficientPrivilegesException(message);
		}
		if (_trigger == null && hasEvent) {
			throw new NotFoundException("Can't find script trigger for " + formatScopeEntityIdAndEvent(projectId, eventId) );
		}
		
	}

	private String formatScopeEntityIdAndEvent(String projectId, String eventId) {
		final StringBuilder buffer = new StringBuilder();
		if (_trigger != null) {
			final Map<String, String> atoms = Scope.decode(_trigger.getAssociation());
			if (atoms.get("scope").equals(Scope.Site.code())) {
				buffer.append("site");
			} else {
				buffer.append("project ").append(atoms.get("entityId"));
			}
			if (_trigger.getEvent() != null) {
				buffer.append(" and event ").append(_trigger.getEvent());
			} else {
				buffer.append(", no event");
			}
		} else {
			if (getScope() == Scope.Site) {
				buffer.append("site");
			} else {
				buffer.append("project ").append(getProjectId(projectId));
			}
			if (StringUtils.isNotBlank(eventId)) {
				buffer.append(" and event ").append(eventId);
			} else {
				buffer.append(", no event");
			}
		}
		return buffer.toString();
	}
	
	protected void validateProjectAccess(final String projectId, UserI user) throws NotFoundException, InitializationException {
        final XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
        if (project == null) {
            throw new NotFoundException("Can't find project with ID: " + getProjectId(projectId));
        }
        try {
            if ((!project.canEdit(user)) || !project.canRead(user)) {
                final String message = "User " + user.getLogin() + " attempted to access project " + getProjectId(projectId) + " with insufficient privileges.";
                log.warn(message);
                throw new InsufficientPrivilegesException(message);
            }
        } catch(Exception e){
            throw new InitializationException("Something went wrong accessing project info for " + getProjectId(projectId));
        }
    }

	private T listScriptTriggers(String projectId) {
		Hashtable<String, Object> params = new Hashtable<>();
		final boolean restrictToScope;
		if (getScope() == Scope.Site) {
			//NEED TO CHNAGE
			final List<String> segments = new ArrayList<>();
					//getRequest().getResourceRef().getSegments();
			final String function = segments.get(segments.size() - 1);
			if (!function.equals("triggers")) {
				params.put("scope", Scope.Site);
				restrictToScope = true;
			} else {
				restrictToScope = false;
			}
		} else {
			params.put("scope", getScope());
			params.put("projectId",projectId);
			restrictToScope = true;
		}
		List<ScriptTrigger> triggers = getScripTriggers(restrictToScope, projectId);
		
		if(false) {
			return  (T) getFilteredtriggers(triggers);
		}else {
			return (T) getScriptTiggerData(triggers);
		}
	}
	
	private List<ScriptTrigger> getScripTriggers(boolean restrictToScope, String projectId) {
		List<ScriptTrigger> triggers = new ArrayList<>();
		if (!restrictToScope) {
			triggers = _scriptTriggerService.getAll();
		} else if (getScope() == Scope.Site) {
			triggers = _scriptTriggerService.getSiteTriggers();
		} else {
			triggers = _scriptTriggerService.getByScope(getScope(), projectId);
		}
		return triggers;
	}

	@SuppressWarnings("unchecked")
	private T getScriptTiggerData(List<ScriptTrigger> triggers) {
		List<ScriptTriggerDto>ScriptTriggerDtos = new ArrayList<>();
		for (final ScriptTrigger trigger : triggers) {
		final Map<String, String> atoms = Scope.decode(trigger.getAssociation());
		final String scope = atoms.get("scope");
		final String entityId = scope.equals(Scope.Site.code()) ? "" : atoms.get("entityId");
		ScriptTriggerDtos.add(ScriptTriggerDto.builder()
				.id(String.valueOf(trigger.getId()))
				.triggerId(trigger.getTriggerId())
				.scope(scope)
				.entityId(entityId)
				.srcEventClass(trigger.getSrcEventClass())
				.event(trigger.getEvent())
				.eventFilters(trigger.getEventFiltersAsMap())
				.scriptId(trigger.getScriptId())
				.description(trigger.getDescription())
				.build());
		}
		return (T)ScriptTriggerDtos;
	}

	private T getFilteredtriggers(List<ScriptTrigger> triggers) {
		List<ScriptTrigger> filteredtriggers = new ArrayList<ScriptTrigger>();
		String sourceProject = null;
				//getRequest().getResourceRef().getQueryAsForm().getFirst("targetProjectName").getValue();
		final List<ScriptTrigger> sourcetriggers = _scriptTriggerService.getByScope(Scope.Project, sourceProject);

		if (sourcetriggers != null && sourcetriggers.size() < 1) {
				return getScriptTiggerData(triggers);
		}else {

			//Match all attributes except description to find duplicate event.
			for (final ScriptTrigger trigger : triggers) {
				for (final ScriptTrigger srctrigger : sourcetriggers) {
					if (srctrigger.getEvent().replace(EXECUTED_SCRIPT, "")
							.equals(trigger.getEvent().replace(EXECUTED_SCRIPT, ""))
							&& srctrigger.getSrcEventClass().equals(trigger.getSrcEventClass())
							&& srctrigger.getScriptId().equals(trigger.getScriptId())
							//&& srctrigger.getDescription().equals(trigger.getDescription())
							&& srctrigger.getEventFiltersAsMap().toString()
									.equals(trigger.getEventFiltersAsMap().toString())) {
						filteredtriggers.add(trigger);
					}
				}
			}
			Collection<ScriptTrigger> eventsToDisplay = CollectionUtils.subtract(triggers, filteredtriggers);
			return getScriptTiggerData(new ArrayList<>(eventsToDisplay));
		}
	}

	protected Scope getScope() {
        return _scope == null ? Scope.Site : _scope;
    }

	//NEED TO ADD IMPL
	private ScriptTrigger getScriptTrigger() {
		return null;
	}
	

	protected boolean hasProjectId() {
        return _hasProjectId;
    }
    protected String getProjectId(String projectId) {
        return _projectId;
    }
    protected void setProjectId(final String projectId) {
        _projectId = projectId;
        _hasProjectId = StringUtils.isNotBlank(_projectId);
    }
	
	private String getProjectIdWithAssociation(String projectId) {
		if (StringUtils.isNotBlank(_trigger.getAssociation())) {
			setAssociation(_trigger.getAssociation(), projectId);
			projectId = getScope() == Scope.Site ? null : projectId;
		} else {
			projectId = null;
		}
		return projectId;
	}
	
	 protected void setAssociation(final String association, String projectId) {
	        final Map<String, String> atoms = Scope.decode(association);
	        _scope = Scope.getScope(atoms.get("scope"));
	        projectId = _scope == Scope.Site ? null : atoms.get("entityId");
	    }

	private ScriptTrigger getTrigger(boolean hasId, String id, String triggerId) throws NotFoundException {
		_trigger = (hasId) ? _scriptTriggerService.getById(id)
				: _scriptTriggerService.getByTriggerId(triggerId);
		if (_trigger == null) {
			throw new NotFoundException( "Can't find script trigger with ID: " + triggerId);
		}
		return _trigger;
	}

	private void validateScop(boolean hasEntityId, boolean hasProjectId, String entityId, String projectId) {
		 if (!hasEntityId && !hasProjectId) {
	            _scope = null;
	            _projectId = null;
	            _hasProjectId = false;
	        } else if (hasEntityId && entityId.equalsIgnoreCase(Scope.Site.code())) {
	            _scope = Scope.Site;
	            _projectId = null;
	            _hasProjectId = false;
	        }else {
	        	final Map<String, String> values;
	            if (hasEntityId) {
	                final Map<String, String> entityProps = Scope.decode(entityId);
	                _scope = Scope.getScope(entityProps.get("scope"));
	                values = validateEntityId(entityProps.get("entityId"));
	            } else {
	                _scope = Scope.Project;
	                values = validateEntityId(projectId);
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
	}

	//NEED TO ADD IMPL
	private Map<String, String> validateEntityId(String string) {
		return null;
	}
	
	private Scope _scope;
	private String  _projectId;
	private boolean _hasProjectId;
	private final ScriptTriggerService _scriptTriggerService;
	private  ScriptTrigger _trigger;
	private static final String KEY_PROJECTID = "projectId";
	private static final String EXECUTED_SCRIPT = "Executed script ";
	private static final String TOOL_NAME_AUTOMATION_UPLOADER = "automation_uploader";
	private static final String PATH_CONFIGURATION = "configuration";
	private static final String EVENT = "event";
	private static final String EVENT_TRIGGER_ID = "eventTriggerId";
}
