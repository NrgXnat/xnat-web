package org.nrg.xnat.services.script.trigger.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.automation.entities.ScriptTrigger;
import org.nrg.automation.services.ScriptTriggerService;
import org.nrg.framework.constants.Scope;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.script.trigger.AutoHandlerScriptTriggerService;
import org.nrg.xnat.services.script.trigger.utils.ScriptTriggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("rawtypes")
@Service
public class AutoHandlerScriptTriggerServiceImpl<T> implements AutoHandlerScriptTriggerService {

	@Autowired
	public AutoHandlerScriptTriggerServiceImpl(final ScriptTriggerService scriptTriggerService) {
		this._scriptTriggerService = scriptTriggerService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T findScriptTrigger(UserI user, String entityId, String projectId) {
		final boolean hasEntityId = StringUtils.isNotBlank(entityId);
        final boolean hasProjectId = StringUtils.isNotBlank(projectId);
        
        validateScop(hasEntityId, hasProjectId, entityId);
        
        //Need to add IMPL
		ScriptTrigger trigger = getScriptTrigger();
		
		if (trigger != null) {
			return (T) trigger;
		}else {
			return (T) listScriptTriggers(projectId);
		}
	}

	
	private void validateScop(boolean hasEntityId, boolean hasProjectId, String entityId) {
		 if (!hasEntityId && !hasProjectId) {
	            _scope = null;
	        } else if (hasEntityId && entityId.equalsIgnoreCase(Scope.Site.code())) {
	            _scope = Scope.Site;
	        } 
	}

	private T listScriptTriggers(String projectId) {
		Hashtable<String, Object> params = new Hashtable<>();
		final boolean restrictToScope;
		if (getScope() == Scope.Site) {
			final List<String> segments = null;
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

	private T getScriptTiggerData(List<ScriptTrigger> triggers) {
		List<ScriptTriggerUtils>scriptTriggerUtils = new ArrayList<>();
		for (final ScriptTrigger trigger : triggers) {
		final Map<String, String> atoms = Scope.decode(trigger.getAssociation());
		final String scope = atoms.get("scope");
		final String entityId = scope.equals(Scope.Site.code()) ? "" : atoms.get("entityId");
		scriptTriggerUtils.add(ScriptTriggerUtils.builder()
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
		return (T)scriptTriggerUtils;
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
	
	private Scope _scope;
	private final ScriptTriggerService _scriptTriggerService;
	private static final String EXECUTED_SCRIPT = "Executed script ";
}
