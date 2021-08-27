package org.nrg.xapi.script;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.nrg.automation.entities.Script;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NrgServiceException;
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
    @XapiRequestMapping(value = {"/automation/scripts","/automation/scriptVersions"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ScriptDto> getAllScripts() throws NotFoundException, InitializationException, InsufficientPrivilegesException, DataFormatException {
    	log.debug("User {} requested automation script with ID {}", getSessionUser().getUsername());
        return _automationScriptService.findAll(getSessionUser());
    }
	
	@ApiOperation(value = "update the requested  automation script", notes = "Returns the  automation script with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested scriptId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested automation script wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/scripts/{scriptId}", "/automation/scripts/{scriptId}/{versionId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = PUT)
    public void updateScriptByScriptIdORVersionId(@ApiParam(value = "The ID of the script.") @PathVariable final String scriptId,
    		@ApiParam(value = "The ID of the version.") @PathVariable(required = false) final String versionId,
    		@ApiParam(value = "The request object")Script script) throws NrgServiceException {
    	log.debug("User {} requested automation script with ID {}", getSessionUser().getUsername(), scriptId);
         _automationScriptService.updateScript(getSessionUser(), scriptId, script);
    }
	
	
	@ApiOperation(value = "Delete the requested  automation script", notes = "Returns the  automation script with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested scriptId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested automation script wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/scripts/{scriptId}", "/automation/scripts/{scriptId}/{versionId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = DELETE)
    public void deleteScriptByScriptIdORVersionId(@ApiParam(value = "The ID of the script.") @PathVariable final String scriptId,
    		@ApiParam(value = "The ID of the version.") @PathVariable(required = false) final String versionId) throws NrgServiceException, DataFormatException  {
    	log.debug("User {} requested automation script with ID {}", getSessionUser().getUsername(), scriptId);
         _automationScriptService.deleteScript(getSessionUser(), scriptId);
    }
	
	@ApiOperation(value = "Gets the requested  automation script version", notes = "Returns the  automation script version with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested handlers."),
    	           @ApiResponse(code = 400, message = "The requested scriptId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested automation script version wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/automation/scriptVersions/{scriptId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<String> getScriptVersionByScriptId(@ApiParam(value = "The ID of the script.") @PathVariable final String scriptId) throws NotFoundException, InitializationException, InsufficientPrivilegesException, DataFormatException {
    	log.debug("User {} requested automation script with ID {}", getSessionUser().getUsername(), scriptId);
        return _automationScriptService.findScriptVersionByScriptId(getSessionUser(), scriptId);
    }
	

	private  final AutomationScriptService _automationScriptService;
}
