package org.nrg.xnat.services.script.trigger;

import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.event.util.ImportEventHandlerResults;

public interface AutoHandlerScriptTriggerService<T> {
	
	public T findScriptTrigger(UserI user, String entityId, String projectId, String triggerId, String eventId, String id) throws NotFoundException, InitializationException, InsufficientPrivilegesException;

	public void update(UserI user, String projectId, String triggerId,String eventId,String id,ImportEventHandlerResults results) throws DataFormatException, ConfigServiceException, NotFoundException, InitializationException, InsufficientPrivilegesException;
	
	public void delete(UserI user,String eventId, String projectId,String triggerId, String id) throws NotFoundException, InitializationException, InsufficientPrivilegesException;
}
