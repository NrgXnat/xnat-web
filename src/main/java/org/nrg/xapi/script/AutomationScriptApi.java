package org.nrg.xapi.script;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.automation.entities.Script;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.dto.script.ScriptDto;
import org.nrg.xnat.services.script.AutomationScriptService;
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

@Api("XNAT script Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class AutomationScriptApi extends AbstractXapiProjectRestController {
	
	@Autowired
    public AutomationScriptApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final AutomationScriptService automationScriptService) {
        super(userManagementService, roleHolder);
        _automationScriptService = automationScriptService;
    }
	
	@ApiOperation(value = "Gets the requested  automation script", notes = "Returns the  automation script with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested scriptId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested automation script wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/scripts/{scriptId}", "/automation/scripts/{scriptId}/{versionId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public Script getScriptByScriptIdORVersionId(@ApiParam(value = "The ID of the script.") @PathVariable final String scriptId,
    		@ApiParam(value = "The ID of the version.") @PathVariable(required = false) final String versionId) throws NotFoundException, InitializationException, InsufficientPrivilegesException, DataFormatException {
    	log.debug("User {} requested automation script with ID {}", getSessionUser().getUsername(), scriptId);
        return _automationScriptService.findByScriptId(getSessionUser(), scriptId, versionId);
    }
	
	@ApiOperation(value = "Gets the requested  automation script", notes = "Returns the  automation script with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested scriptId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested automation script wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/scripts"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ScriptDto> getAllScripts() throws NotFoundException, InitializationException, InsufficientPrivilegesException, DataFormatException {
    	log.debug("User {} requested automation script with ID {}", getSessionUser().getUsername());
        return _automationScriptService.findAll(getSessionUser());
    }

	private  final AutomationScriptService _automationScriptService;
}
