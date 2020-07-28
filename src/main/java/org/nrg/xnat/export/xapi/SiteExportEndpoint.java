package org.nrg.xnat.export.xapi;

import static org.nrg.xdat.security.helpers.AccessLevel.Admin;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.List;

import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.constants.Scope;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.model.endpoint.EndpointDefinition;
import org.nrg.xnat.export.utils.ExportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

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

public class SiteExportEndpoint extends AbstractXapiRestController{

	@Autowired
	public SiteExportEndpoint(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ConfigService configService) {
		super(userManagementService, roleHolder);
		_configService = configService;
	}

	
    @ApiOperation(value = "Add Export Endpoint Definition to Site ")
    @ApiResponses({
    	    @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid parameters"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "add", method = POST,  consumes = MediaType.TEXT_PLAIN_VALUE,  restrictTo = Admin)
    public ResponseEntity<String> addToSite(@RequestBody String jsonbody, @RequestParam(value = "overwrite") final String overwrite) {
		final UserI user = getSessionUser();
		 //TODO: Is the JSON valid as per schema
		 //Add to the Site
		 try {
				ObjectMapper objectMapper = new ObjectMapper();	
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				
			 	EndpointDefinition endPointDefinition = objectMapper.readValue(jsonbody, EndpointDefinition.class);  
		 	 //Save the json to the export tool
			 boolean overwriteBool = Boolean.parseBoolean(overwrite);	
		 	 Configuration configurationForToolAndExportHandler = _configService.getConfig(ExportConstants.TOOL_ID, endPointDefinition.getLabel());
		 	 
		 	 if (configurationForToolAndExportHandler == null || overwriteBool) {
		 		 _configService.replaceConfig(user.getUsername(), "User Added", ExportConstants.TOOL_ID, endPointDefinition.getLabel(), true,objectMapper.writeValueAsString(endPointDefinition), Scope.Site, null );
		 	 }else {
		           return new ResponseEntity<>("Delete existing export handler or set overwrite=true",HttpStatus.BAD_REQUEST);
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
    @XapiRequestMapping(value = "delete", method = DELETE, restrictTo = Admin)
    public ResponseEntity<String> delete(@RequestParam(value = "label", required=true) final String label) {
		 Configuration configurationForToolAndExportHandler = _configService.getConfig(ExportConstants.TOOL_ID, label);
		 if (configurationForToolAndExportHandler != null) {
			 try {
				 _configService.delete(configurationForToolAndExportHandler);
				 return  new ResponseEntity<>("Endpoint disabled", HttpStatus.OK);
			 }catch(Exception e) {
				 return  new ResponseEntity<>("Endpoint could not be disabled", HttpStatus.BAD_REQUEST);
			 }
		 }else {
			 return  new ResponseEntity<>("Export Definition configuration for " + label + " not found.", HttpStatus.BAD_REQUEST);
		 }
    }

    
    @ApiOperation(value = "Get Export Endpoint Definition for a given label")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid parameters"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "get", method = GET,  restrictTo = Admin)
    public ResponseEntity<Configuration> getDefinitionByLabel(@RequestParam(value = "label", required=true) final String label) {
		 Configuration configurationForToolAndExportHandler = _configService.getConfig(ExportConstants.TOOL_ID,label, Scope.Site, null);
		 if (configurationForToolAndExportHandler != null) {
			 try {
				 return  new ResponseEntity<>(configurationForToolAndExportHandler, HttpStatus.OK);
			 }catch(Exception e) {
				 return  new ResponseEntity<>(null, HttpStatus.OK);
			 }
		 }else {
			 return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		 }
    }

    @ApiOperation(value = "Enable a site wide export endpoint")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid parameters"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "enable", method = POST,  restrictTo = Admin)
    public ResponseEntity<Configuration> enable(@RequestParam(value = "label", required=true) final String label, @RequestParam(value = "enabled", required=true) final String enabled) {
		final UserI user = getSessionUser();
		 Configuration configurationForToolAndExportHandler = _configService.getConfig(ExportConstants.TOOL_ID,label, Scope.Site, null);
		 boolean enabledBool = Boolean.parseBoolean(enabled);
		 if (configurationForToolAndExportHandler != null) {
			 try {
				 if (enabledBool)
					 _configService.enable(user.getUsername(), "User enabled", ExportConstants.TOOL_ID, label, Scope.Site, null);
				 else { 
					 _configService.disable(user.getUsername(), "User disabled", ExportConstants.TOOL_ID, label, Scope.Site, null);
					 //All projects which have this endpoint get disabled
					 List<String> projectIds = _configService.getProjects(ExportConstants.TOOL_ID);
					 if (projectIds != null && projectIds.size() > 0) {
						 for (String p : projectIds) {
							Configuration projConfig =  _configService.getConfig(ExportConstants.TOOL_ID, label, Scope.Project, p);
							if (projConfig != null) 
								_configService.disable(user.getUsername(), "User disabled", ExportConstants.TOOL_ID, label, Scope.Project, p);
						 }
					 }
				 } 
				return  new ResponseEntity<>(configurationForToolAndExportHandler, HttpStatus.OK);
			 }catch(ConfigServiceException cse) {
				 return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			 }catch(Exception e) {
				 return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			 }
		 }else {
			 return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		 }
    }

    
    @ApiOperation(value = "List Export Endpoint Definitions")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Invalid parameters"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "list", method = GET, produces = MediaType.APPLICATION_JSON_VALUE,  restrictTo = Admin)
    public ResponseEntity<List<Configuration>> list() {
		List<Configuration> configs = _configService.getConfigsByTool(ExportConstants.TOOL_ID, Scope.Site, null);
		if (configs != null && configs.size() > 0)
			return  new ResponseEntity<>(configs, HttpStatus.OK);
		else {
			List<Configuration> emptyOne = new ArrayList<Configuration>();
			return  new ResponseEntity<>(emptyOne, HttpStatus.OK);
		}
    }

    
    

	private final ConfigService              _configService;

}
