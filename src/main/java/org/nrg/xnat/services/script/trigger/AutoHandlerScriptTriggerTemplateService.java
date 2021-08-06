package org.nrg.xnat.services.script.trigger;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface AutoHandlerScriptTriggerTemplateService<T> {

	public T findScriptTriggerTemplate(UserI user, String templateId, String projectId) throws NotFoundException;
}
