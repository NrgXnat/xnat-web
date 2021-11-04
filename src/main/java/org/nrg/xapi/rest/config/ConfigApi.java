package org.nrg.xapi.rest.config;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Map;

import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xapi.model.config.ConfigModel;
import org.nrg.xnat.services.config.ConfigurationService;
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

@Api("XNAT config Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ConfigApi extends AbstractXapiProjectRestController {
	@Autowired
	public ConfigApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ConfigurationService configurationService) {
		 super(userManagementService, roleHolder);
		_configurationService = configurationService;
	}

	@ApiOperation(value = "Gets the requested  config", notes = "Returns the  project with the specified ID", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested config wasn't found."),
			@ApiResponse(code = 404, message = "The requested config wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<Map<String, String>> getAllConfigs()
			throws NotFoundException, DataFormatException {
		log.debug("User {} requested configs", getSessionUser().getUsername());
		return _configurationService.findAllConfigs(getSessionUser());
	}
	
	@ApiOperation(value = "Gets the requested  config", notes = "Returns the  project with the specified ID", response = Configuration.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested project config wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = { "/config/{toolName}","/projects/{projectId}/config/{toolName}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<Configuration> getAllByToolName(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
												@ApiParam(value = "The value of tool name ") @PathVariable final String toolName)
			throws NotFoundException, DataFormatException {
		log.debug("User {} requested configs with tool Name {}", getSessionUser().getUsername(), toolName);
		return _configurationService.findAllByToolName(getSessionUser(),toolName, projectId);
	}
	
	@ApiOperation(value = "Gets the requested  config", notes = "Returns the  project with the specified ID", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested config wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/config", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<Map<String, String>> getAllProjectConfigs(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested configs", getSessionUser().getUsername());
		return _configurationService.findAllProjectConfigs(getSessionUser(), projectId);
	}
	
	@ApiOperation(value = "Gets the requested  config", notes = "Returns the  project with the specified ID", response = Configuration.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested project config wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = { "/config/{toolName}/{path}","/projects/{projectId}/config/{toolName}/{path}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<Configuration> getAllByToolNameAndPath(@ApiParam(value = "The value of tool name ") @PathVariable final String toolName,
													   @ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
													   @ApiParam(value = "The value  of the path to file.") @PathVariable final String path,
													   @ApiParam(value = "The value  of the defaultToSiteWide.") @RequestParam(name ="defaultToSiteWide" ) final boolean defaultToSiteWide,
													   @ApiParam(value = "The value  of the history.") @RequestParam(name ="history", required = false) final String history,
													   @ApiParam(value = "The value  of the requestVersion.") @RequestParam(name ="requestVersion", required = false) final String requestVersion) throws NotFoundException, DataFormatException {
		log.debug("User {} requested configs with tool Name {}", getSessionUser().getUsername(), toolName);
		return _configurationService.findAllByToolNameAndPath(getSessionUser(),toolName,projectId, path,defaultToSiteWide, history, requestVersion);
	}
	
	@ApiOperation(value = "Update an existing config", notes = "Updates the submitted config.", response = Configuration.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated Configuration."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit Configuration in the specified project"),
                   @ApiResponse(code = 404, message = "The specified Configuration doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/config/{toolName}/{path}",  consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        						produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = PUT)
    public void updateConfiguration(@ApiParam("The ID of the Configuration to be updated") @PathVariable final String projectId,
                                         @ApiParam("The project to be updated.") @RequestBody final ConfigModel config,
                                         @ApiParam(value = "The value of tool name ") @PathVariable final String toolName,
                                         @ApiParam(value = "The value  of the path to file.") @PathVariable final String path,
                                         @ApiParam(value = "The value  of the status.")@RequestParam(name ="status", required = false) String status,
                                         @ApiParam(value = "The value  of the reason.")@RequestParam(name ="reason", required = false) String reason,
                                         @ApiParam(value = "The value  of the unversioned.")@RequestParam(name ="unversioned", required = false) String unversioned) throws InsufficientPrivilegesException, InitializationException, Exception  {
       
        log.debug("User {} requested to update Configuration with projectId {} , with toolName {}", getSessionUser().getUsername(), projectId, toolName);
         _configurationService.updateConfig(getSessionUser(), config, toolName,projectId,path,status,reason, unversioned);
    }
	
	 @ApiOperation(value = "Delete an existing config", notes = "Deletes the specified config.")
	    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified Configuration."),
	    			   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete Configuration in the specified project"),
	                   @ApiResponse(code = 404, message = "The specified Configuration or Configuration doesn't exist"),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = "/projects/{projectId}/config/{toolName}/{path}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
	    public void deleteConfig(@ApiParam("The ID of the Configuration to be updated") @PathVariable final String projectId,
	    					  @ApiParam("The project to be updated.") @RequestBody final ConfigModel config,
	    					  @ApiParam(value = "The value of tool name ") @PathVariable final String toolName,
	    					  @ApiParam(value = "The value  of the path to file.") @PathVariable final String path) throws DataFormatException, InitializationException, NotFoundException, ConfigServiceException, InsufficientPrivilegesException  {
	    	 log.debug("User {} requested to delete project with ID {}", getSessionUser().getUsername(), projectId);
	    	 _configurationService.deleteConfig(getSessionUser(), toolName, projectId, path);
	    }
	
	private final ConfigurationService _configurationService;
}
