package org.nrg.xnat.services.script.trigger;

import javax.servlet.http.HttpServletRequest;

import org.nrg.automation.entities.ScriptTriggerTemplate;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface AutoHandlerScriptTriggerTemplateService<T> {

	public T findScriptTriggerTemplate(UserI user, String templateId, String projectId) throws NotFoundException, InitializationException, InsufficientPrivilegesException;
	
	public void update(UserI user, ScriptTriggerTemplate template, String templateId, HttpServletRequest request) throws InitializationException;
	
}
