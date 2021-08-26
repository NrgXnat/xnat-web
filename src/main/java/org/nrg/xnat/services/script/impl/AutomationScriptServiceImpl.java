package org.nrg.xnat.services.script.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.nrg.automation.entities.Script;
import org.nrg.automation.services.ScriptRunnerService;
import org.nrg.automation.services.ScriptService;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.script.ScriptDto;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.services.script.AutomationScriptService;
import org.nrg.xnat.services.script.trigger.utils.AutomationScriptTriggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AutomationScriptServiceImpl extends AutomationScriptTriggerUtils implements AutomationScriptService{

	@Autowired
	public AutomationScriptServiceImpl(ScriptService scriptService, final ScriptRunnerService runnerService) {
		this._scriptService = scriptService;
		this._runnerService = runnerService;
	}
	@Override
	public Script findByScriptId(UserI user, String scriptId, String version) throws NotFoundException, DataFormatException {
		if(StringUtils.isBlank(scriptId)) {
			throw new DataFormatException("The requested script ID" + scriptId + " wasn't found ");
		}
			if (StringUtils.isNotBlank(version)) {
				return (Script) _scriptService.getVersion(scriptId, version);
			} else {
				// They're requesting a specific script, so return that to them.
				Script script = getScript(scriptId);

				if (script == null && scriptId.equalsIgnoreCase(PrearcDatabase.SPLIT_PETMR_SESSION_ID)) {
					script = PrearcDatabase.DEFAULT_SPLIT_PETMR_SESSION_SCRIPT;
				}
				if (script != null) {
					return script;
				} else {
					throw new NotFoundException("Script data wasn't found");
			}
		}
	}
	
	
	@Override
	public List<ScriptDto> findAll(UserI user) {
		return getScriptList();
	}
	
	@Override
	public void updateScript(UserI user, String scriptId, Script script) throws NrgServiceException {
		  if (log.isDebugEnabled()) {
              log.debug("Preparing to PUT script: " + scriptId);
          }
          putScript(user, scriptId, script);
	}
	
	@Override
	public void deleteScript(UserI user, String scriptId) throws NrgServiceException {
		if (log.isDebugEnabled()) {
            log.debug("Preparing to delete script: " + scriptId + " and its associated triggers.");
        }
        _runnerService.deleteScript(scriptId);
        recordAutomationEvent(scriptId, SITE_SCOPE, "Delete", Script.class, user);
	}
	
	private void putScript(UserI user, String scriptId, Script script) throws NrgServiceException {
		ObjectMapper mapper = new ObjectMapper();
		
		final Properties properties = mapper.convertValue(script, Properties.class);

		if (properties.containsKey("scriptId")) {
			properties.remove("scriptId");
		}
		_runnerService.setScript(scriptId, properties);
		recordAutomationEvent(scriptId, SITE_SCOPE, "Update", Script.class, user);
	}
	
	private List<ScriptDto> getScriptList() {
		List<ScriptDto> scriptDtos = new ArrayList<>();
		final List<Script> scripts = _scriptService.getAll();
		for(Script script : scripts) {
			scriptDtos.add(ScriptDto.builder().scriptId(script.getScriptId())
					.scriptLabel(script.getScriptLabel())
					.language(script.getLanguage())
					.description(script.getDescription())
					.build());
		}
		
		return scriptDtos;
	}
	private Script getScript(String scriptId) {
        return _runnerService.getScript(scriptId);
    }

	
	private final ScriptService _scriptService;
	private final ScriptRunnerService _runnerService;

}
