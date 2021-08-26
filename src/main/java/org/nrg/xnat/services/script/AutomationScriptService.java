package org.nrg.xnat.services.script;

import java.util.List;

import org.nrg.automation.entities.Script;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.script.ScriptDto;

public interface AutomationScriptService {

	Script findByScriptId(UserI user, String scriptId, String version) throws NotFoundException, DataFormatException;

	List<ScriptDto> findAll(UserI user);
	
	void updateScript(UserI user, String scriptId, Script script) throws NrgServiceException;
	
	void deleteScript(UserI user, String scriptId) throws NrgServiceException;
}
