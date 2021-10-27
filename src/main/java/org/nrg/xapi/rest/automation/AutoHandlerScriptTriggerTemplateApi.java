package org.nrg.xapi.rest.automation;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.nrg.automation.entities.ScriptTriggerTemplate;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.script.trigger.AutoHandlerScriptTriggerTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.*;

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

    @ApiOperation(value = "Gets the requested  Trigger Template handlers", notes = "Returns the  Trigger Template handlers with the specified ID", response = ScriptTriggerTemplate.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested handlers wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/templates", "/automation/templates/{templateId}", "/projects/{projectId}/automation/templates",
                                 "/projects/{projectId}/automation/templates/{templateId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public T getScriptTriggerTemplates(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                       @ApiParam(value = "The ID of the template.") @PathVariable(required = false) final String templateId) throws NotFoundException, InitializationException, InsufficientPrivilegesException {
        log.debug("User {} requested automation handlers with ID {}", getSessionUser().getUsername(), projectId);
        return _scriptTriggerTemplateService.findScriptTriggerTemplate(getSessionUser(), templateId, projectId);
    }

    @ApiOperation(value = "Update the requested  handlers template", notes = "Returns the  handlers template with the specified ID")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers template."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested handlers template wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/templates", "/automation/templates/{templateId}", "/projects/{projectId}/automation/templates",
                                 "/projects/{projectId}/automation/templates/{templateId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = PUT)
    public void updateScriptTriggersTemplate(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                             @ApiParam(value = "The ID of the template.") @PathVariable(required = false) final String templateId,
                                             @ApiParam(value = "The request body") @RequestBody ScriptTriggerTemplate template,
                                             @ApiParam("Http servlet request") HttpServletRequest request) throws InitializationException {
        log.debug("User {} requested automation handlers with ID {}", getSessionUser().getUsername(), projectId);
        _scriptTriggerTemplateService.update(getSessionUser(), template, templateId, request);
    }

    @ApiOperation(value = "Delete the requested  handlers template", notes = "Returns the  handlers template with the specified ID")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers template."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested handlers template wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/templates", "/automation/templates/{templateId}", "/projects/{projectId}/automation/templates",
                                 "/projects/{projectId}/automation/templates/{templateId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = DELETE)
    public void deleteScriptTriggersTemplate(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                             @ApiParam(value = "The ID of the template.") @PathVariable(required = false) final String templateId) throws NotFoundException {
        log.debug("User {} requested automation handlers with ID {}", getSessionUser().getUsername(), projectId);
        _scriptTriggerTemplateService.delete(getSessionUser(), templateId);
    }

    private final AutoHandlerScriptTriggerTemplateService<T> _scriptTriggerTemplateService;
}
