package org.nrg.xapi.rest.automation;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
//import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.event.util.ImportEventHandlerResults;
import org.nrg.xnat.services.script.trigger.AutoHandlerScriptTriggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;


@Api("XNAT script Trigger Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class AutoHandlerScriptTriggerApi<T> extends AbstractXapiProjectRestController {
	
	@Autowired
    public AutoHandlerScriptTriggerApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final AutoHandlerScriptTriggerService<T> scriptTriggerService) {
        super(userManagementService, roleHolder);
        _scriptTriggerService = scriptTriggerService;
    }
	
	@ApiOperation(value = "Gets the requested  handlers", notes = "Returns the  handlers with the specified ID", response = XnatProjectdataI.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested handlers wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/handlers","/automation/handlers/{eventId}","/automation/triggers", 
    							"/automation/triggers/{triggerId}","projects/{projectId}/automation/handlers",
    							"/projects/{projectId}/automation/handlers/{eventId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public T getScriptTriggers(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
    		@ApiParam(value = "The ID of the entity.") @PathVariable(required = false) final String entityId,
    		@ApiParam(value = "The ID of the trigger.") @PathVariable(required = false) final String triggerId,
    		@ApiParam(value = "The ID of the event.") @PathVariable(required = false) final String eventId,
    		@ApiParam(value = "The value of the ID.") @RequestParam(required = false) final String id) throws NotFoundException, InitializationException, InsufficientPrivilegesException, DataFormatException {
    	log.debug("User {} requested automation handlers with ID {}", getSessionUser().getUsername(), projectId);
        return _scriptTriggerService.findScriptTrigger(getSessionUser(), entityId, projectId,triggerId,eventId,id);
    }
	
	@ApiOperation(value = "Update the requested  handlers", notes = "Returns the  handlers with the specified ID", response = XnatProjectdataI.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested handlers wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/handlers","/automation/handlers/{eventId}","/automation/triggers", 
    							"/automation/triggers/{triggerId}","projects/{projectId}/automation/handlers",
    							"/projects/{projectId}/automation/handlers/{eventId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = PUT)
    public void updateScriptTriggers(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
    		@ApiParam(value = "The ID of the entity.") @PathVariable(required = false) final String entityId,
    		@ApiParam(value = "The ID of the trigger.") @PathVariable(required = false) final String triggerId,
    		@ApiParam(value = "The ID of the event.") @PathVariable(required = false) final String eventId,
    		@ApiParam(value = "The request body") @RequestBody ImportEventHandlerResults results,
    		@ApiParam(value = "The value of the ID.") @RequestParam(required = false) final String id) throws ConfigServiceException, DataFormatException, NotFoundException, InitializationException, InsufficientPrivilegesException  {
    	log.debug("User {} requested automation handlers with ID {}", getSessionUser().getUsername(), projectId);
         _scriptTriggerService.update(getSessionUser(), projectId, triggerId,eventId,id, results);
    }
	
	@ApiOperation(value = "Delete the requested  handlers", notes = "Returns the  handlers with the specified ID", response = XnatProjectdataI.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested handlers wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/handlers","/automation/handlers/{eventId}","/automation/triggers", 
    							"/automation/triggers/{triggerId}","projects/{projectId}/automation/handlers",
    							"/projects/{projectId}/automation/handlers/{eventId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = DELETE)
    public void  deleteScriptTriggers(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
    		@ApiParam(value = "The ID of the entity.") @PathVariable(required = false) final String entityId,
    		@ApiParam(value = "The ID of the trigger.") @PathVariable(required = false) final String triggerId,
    		@ApiParam(value = "The ID of the event.") @PathVariable(required = false) final String eventId,
    		@ApiParam(value = "The value of the ID.") @RequestParam(required = false) final String id) throws NotFoundException, InitializationException, InsufficientPrivilegesException {
    	log.debug("User {} requested automation handlers with ID {}", getSessionUser().getUsername(), projectId);
         _scriptTriggerService.delete(getSessionUser(), eventId, projectId, triggerId, id);
    }
	
	private final AutoHandlerScriptTriggerService<T> _scriptTriggerService;

}
