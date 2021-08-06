package org.nrg.xapi.script.trigger;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.script.trigger.AutoHandlerScriptTriggerTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT script Trigger Template Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class AutoHandlerScriptTriggerTemplateApi<T> extends AbstractXapiProjectRestController {
	
	@Autowired
    public AutoHandlerScriptTriggerTemplateApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final AutoHandlerScriptTriggerTemplateService<T> scriptTriggerTemplateService) {
        super(userManagementService, roleHolder);
        _scriptTriggerTemplateService = scriptTriggerTemplateService;
    }
	
	@ApiOperation(value = "Gets the requested  Trigger Template handlers", notes = "Returns the  Trigger Template handlers with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested handlers wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/templates","/automation/templates/{templateId}", "/projects/{projectId}/automation/templates", 
    							"/projects/{projectId}/automation/templates/{templateId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public T getScriptTriggerTemplates(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
    		@ApiParam(value = "The ID of the template.") @PathVariable(required = false) final String templateId) throws NotFoundException, InitializationException, InsufficientPrivilegesException {
    	log.debug("User {} requested automation handlers with ID {}", getSessionUser().getUsername(), projectId);
        return _scriptTriggerTemplateService.findScriptTriggerTemplate(getSessionUser(), templateId, projectId);
    }
	
	private final AutoHandlerScriptTriggerTemplateService<T> _scriptTriggerTemplateService;
}
