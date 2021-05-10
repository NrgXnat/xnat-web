package org.nrg.xapi.config;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import java.util.Map;

import org.nrg.config.entities.Configuration;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.config.ConfigurationService;
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

	@ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested project wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<Map<String, String>> getAllConfigs()
			throws NotFoundException, DataFormatException {
		log.debug("User {} requested configs", getSessionUser().getUsername());
		return _configurationService.findAllConfigs(getSessionUser());
	}
	
	@ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = Configuration.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested project wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/config/{toolName}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<Configuration> getByToolName(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The value of tool name ") @PathVariable final String toolName)
			throws NotFoundException, DataFormatException {
		log.debug("User {} requested configs with tool Name {}", getSessionUser().getUsername(), toolName);
		return _configurationService.findByToolName(getSessionUser(),toolName, projectId);
	}

	private final ConfigurationService _configurationService;
}
