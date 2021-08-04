package org.nrg.xnat.services.script.trigger;

import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface AutoHandlerScriptTriggerService<T> {
	
	public T findScriptTrigger(UserI user, String entityId, String projectId, String triggerId, String eventId, String id) throws NotFoundException, InitializationException, InsufficientPrivilegesException;
}
