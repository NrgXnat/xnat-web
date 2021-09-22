package org.nrg.xnat.services.script.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.automation.entities.Script;
import org.nrg.automation.services.ScriptRunnerService;
import org.nrg.automation.services.ScriptService;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.script.ScriptDto;
import org.nrg.xnat.services.script.AutomationScriptService;
import org.nrg.xnat.services.script.trigger.utils.AutomationScriptTriggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AutomationScriptServiceImpl extends AutomationScriptTriggerUtils implements AutomationScriptService {

    @Autowired
    public AutomationScriptServiceImpl(ScriptService scriptService, final ScriptRunnerService runnerService) {
        this._scriptService = scriptService;
        this._runnerService = runnerService;
    }

    @Override
    public Script findByScriptId(UserI user, String scriptId, String version) throws NotFoundException, DataFormatException {
        if (StringUtils.isBlank(scriptId)) {
            throw new DataFormatException("The requested script ID" + scriptId + " wasn't found ");
        }
        if (StringUtils.isNotBlank(version)) {
            return (Script) _scriptService.getVersion(scriptId, version);
        } else {
            // They're requesting a specific script, so return that to them.
            Script script = getScript(scriptId);

            if (script == null && scriptId.equalsIgnoreCase(ScriptService.SPLIT_PETMR_SESSION_ID)) {
                script = ScriptService.DEFAULT_SPLIT_PETMR_SESSION_SCRIPT;
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
    public void deleteScript(UserI user, String scriptId) throws NrgServiceException, DataFormatException {
        if (log.isDebugEnabled()) {
            log.debug("Preparing to delete script: " + scriptId + " and its associated triggers.");
        }
        if (StringUtils.isBlank(scriptId)) {
            throw new DataFormatException("The requested script ID" + scriptId + " wasn't found ");
        }
        _runnerService.deleteScript(scriptId);
        recordAutomationEvent(scriptId, SITE_SCOPE, "Delete", Script.class, user);
    }

    @Override
    public List<String> findScriptVersionByScriptId(UserI user, String scriptId) {
        return _scriptService.getVersions(scriptId);
    }

    private void putScript(UserI user, String scriptId, Script script) throws NrgServiceException {
        ObjectMapper mapper = new ObjectMapper();

        final Properties properties = mapper.convertValue(script, Properties.class);
        properties.remove("scriptId");

        _runnerService.setScript(scriptId, properties);
        recordAutomationEvent(scriptId, SITE_SCOPE, "Update", Script.class, user);
    }

    private List<ScriptDto> getScriptList() {
        return _scriptService.getAll().stream().map(script -> ScriptDto.builder().scriptId(script.getScriptId())
                                                                       .scriptLabel(script.getScriptLabel())
                                                                       .language(script.getLanguage())
                                                                       .description(script.getDescription())
                                                                       .build()).collect(Collectors.toList());
    }

    private Script getScript(String scriptId) {
        return _runnerService.getScript(scriptId);
    }


    private final ScriptService       _scriptService;
    private final ScriptRunnerService _runnerService;


}
