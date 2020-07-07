package org.nrg.xnat.export.xapi;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.nrg.config.entities.Configuration;
import org.nrg.config.entities.ConfigurationData;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.constants.Scope;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.json.ExportExclusionStrategy;
import org.nrg.xnat.export.model.endpoint.EndpointDefinition;
import org.nrg.xnat.export.utils.ExportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@XapiRestController
@RequestMapping(value = "/exportendpoint")
@Api("Export Endpoint API")
@Slf4j

/*
 * Class handles Export Endpoint Definitions
 */

public class ExportEndpoint extends AbstractXapiRestController{

	@Autowired
	public ExportEndpoint(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ConfigService configService, final SerializerService serializer, final JdbcTemplate jdbcTemplate) {
		super(userManagementService, roleHolder);
		_configService = configService;
		_serializer = serializer;
		_jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}

	
    @ApiOperation(value = "Add Export Endpoint Definition to Site ")
    @ApiResponses({
    	    @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid parameters"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "add", method = POST,  consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> addToSite(@RequestBody String jsonbody) {
		final UserI user = getSessionUser();
		//User should be an admin
		 if (!getRoleHolder().isSiteAdmin(user)) {
	           return new ResponseEntity<>("Only Site Admins can add an export endpoint definition",HttpStatus.FORBIDDEN);
	     }
		 //TODO: Is the JSON valid as per schema
		 //Add to the Site
		 try {
				ObjectMapper objectMapper = new ObjectMapper();	
			 	EndpointDefinition endPointDefinition = objectMapper.readValue(jsonbody, EndpointDefinition.class);  

		 	 //Save the json to the export tool
		 	 Configuration configurationForToolAndExportHandler = _configService.getConfig(ExportConstants.TOOL_ID, endPointDefinition.getExportHandler());
		 	 if (configurationForToolAndExportHandler == null) {
		 		 _configService.replaceConfig(user.getUsername(), "User Added", ExportConstants.TOOL_ID, endPointDefinition.getExportHandler(), true,objectMapper.writeValueAsString(endPointDefinition) );
		 	 }else {
		           return new ResponseEntity<>("Delete existing export handler",HttpStatus.BAD_REQUEST);
		 	 }
		 }catch(Exception e) {
			   log.error("Possibly invalid json ", e);
	           return new ResponseEntity<>("Probably incorrect JSON",HttpStatus.BAD_REQUEST);
		 }
		 
		return  new ResponseEntity<>("Destination added", HttpStatus.OK);
    }


    
    @ApiOperation(value = "Delete Export Endpoint Definition")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid parameters"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "delete", method = DELETE)
    public ResponseEntity<String> delete(@RequestParam(value = "exportHandler", required=true) final String exportHandler) {
		final UserI user = getSessionUser();
		//User should be an admin
		 if (!getRoleHolder().isSiteAdmin(user)) {
	           return new ResponseEntity<>("Only Site Admins can add an export endpoint definition",HttpStatus.FORBIDDEN);
	     }
		 Configuration configurationForToolAndExportHandler = _configService.getConfig(ExportConstants.TOOL_ID, exportHandler);
		 if (configurationForToolAndExportHandler != null) {
			 try {
				 _configService.disable(user.getUsername(), "User requested", ExportConstants.TOOL_ID, exportHandler);
				 return  new ResponseEntity<>("Endpoint disabled", HttpStatus.OK);
			 }catch(Exception e) {
				 return  new ResponseEntity<>("Endpoint could not be disabled", HttpStatus.OK);
			 }
		 }else {
			 return  new ResponseEntity<>("Export Handler configuration for " + exportHandler + " not found.", HttpStatus.BAD_REQUEST);
		 }
    }

    @ApiOperation(value = "List Export Endpoint Definitions")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid parameters"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "list", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Configuration>> list() {
		final UserI user = getSessionUser();
		List<Configuration> configs = _configService.getConfigsByTool(ExportConstants.TOOL_ID);
		if (configs != null && configs.size() > 0)
			return  new ResponseEntity<>(configs, HttpStatus.OK);
		else
			return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


	private final ConfigService              _configService;
	private final SerializerService          _serializer;
	private final NamedParameterJdbcTemplate _jdbcTemplate;



}
