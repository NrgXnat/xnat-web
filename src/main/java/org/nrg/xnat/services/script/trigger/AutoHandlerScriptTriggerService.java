package org.nrg.xnat.services.script.trigger;

import org.nrg.xft.security.UserI;

public interface AutoHandlerScriptTriggerService<T> {
	
	public T findScriptTrigger(UserI user, String entityId, String projectId);
}
