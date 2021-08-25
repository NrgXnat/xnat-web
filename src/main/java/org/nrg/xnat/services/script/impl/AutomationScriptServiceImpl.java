package org.nrg.xnat.services.script.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.automation.entities.Script;
import org.nrg.automation.services.ScriptRunnerService;
import org.nrg.automation.services.ScriptService;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.script.ScriptDto;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.services.script.AutomationScriptService;
import org.nrg.xnat.services.script.trigger.utils.AutomationScriptTriggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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
