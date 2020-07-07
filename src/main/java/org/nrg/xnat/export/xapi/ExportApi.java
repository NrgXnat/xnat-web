package org.nrg.xnat.export.xapi;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.nrg.config.entities.Configuration;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.constants.Scope;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.credentials.SiteCredentials;
import org.nrg.xnat.export.exception.ExporterNotFoundException;
import org.nrg.xnat.export.interfaces.ExportManagerI;
import org.nrg.xnat.export.interfaces.ExporterI;
import org.nrg.xnat.export.jms.requests.ExportRequest;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.manifest.TransportManifest;
import org.nrg.xnat.export.model.endpoint.EndpointDefinition;
import org.nrg.xnat.export.utils.ExportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RequestMapping(value = "/export")
@Api("Export API")
@Slf4j
/*
 * Class which handles REST calls to export
 */

public class ExportApi extends AbstractXapiProjectRestController {
	    
	    private final ExportManagerI _exportManager;

	    @Autowired
	    public ExportApi(final ExportManagerI exportManager,
	                     final UserManagementServiceI userManagementService,
	                     final RoleHolder roleHolder,
	                     final ConfigService configService,
	                     final SerializerService serializerService
	                     ) {
	        super(userManagementService, roleHolder);
	        this._exportManager = exportManager;
	        this._configService = configService;
	        this._serializerService = serializerService;
	    }
	    
	    
	    @ApiOperation(value = "Setup Project preferences for a given Export Endpoint ")
	    @ApiResponses({
	    	    @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "/endpoint/projects/{projectId}", method = POST,  consumes = MediaType.TEXT_PLAIN_VALUE)
	    public ResponseEntity<String> addExporterToProject(@PathVariable("projectId") String projectId,final @RequestBody String jsonbody) {
			final UserI user = getSessionUser();
			//TODO: Is the JSON valid as per schema
			 //Add to the Site
			 try {
					HttpStatus status = canDeleteProject(projectId);
					if (status != null) {
			            return new ResponseEntity<>("Either project " + projectId + " is not found or user does not have owner access to the project",status);
			        }
				ObjectMapper objectMapper = new ObjectMapper();	
			 	EndpointDefinition endPointDefinition = objectMapper.readValue(jsonbody, EndpointDefinition.class);  
			 	_configService.replaceConfig(user.getUsername(), "Export Endpoint Added to Project", ExportConstants.TOOL_ID, endPointDefinition.getExportHandler(),true, jsonbody, Scope.Project, projectId);
			 }catch(Exception e) {
				   log.error("Possibly invalid json ", e);
		           return new ResponseEntity<>("Probably incorrect JSON",HttpStatus.BAD_REQUEST);
			 }
			 
			return  new ResponseEntity<>("Destination added", HttpStatus.OK);
	    }

	    @ApiOperation(value = "Setup Project preferences for a given Export Endpoint ")
	    @ApiResponses({
	    	    @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "/endpoint/list/{projectId}",  method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<List<Configuration>> list(@PathVariable("projectId") String projectId) {
			//TODO: Is the JSON valid as per schema
			 //Add to the Site
			 try {
					HttpStatus status = canDeleteProject(projectId);
					if (status != null) {
			            return new ResponseEntity<>(null,status);
			        }

					List<Configuration> configs = _configService.getConfigsByTool(ExportConstants.TOOL_ID, Scope.Project, projectId);
					if (configs != null && configs.size() > 0)
						return  new ResponseEntity<>(configs, HttpStatus.OK);
					else
						return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			 }catch(Exception e) {
				   log.error("Possibly invalid json ", e);
		           return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			 }
	    }

	    
	    @ApiOperation(value = "Export given list of datatypes")
	    @ApiResponses({
	            @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "/projects/{projectId}", method = POST, produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	    public ResponseEntity<String> export(@PathVariable("projectId") String projectId,final @RequestBody String jsonbody) {
	       
			final UserI user = getSessionUser();

			try {
				
				//Does the user have the rights on the Project to be able to export?
				HttpStatus status = canDeleteProject(projectId);
				if (status != null) {
		            return new ResponseEntity<>("Either project " + projectId + " is not found or user does not have owner access to the project",status);
		        }
				ObjectMapper objectMapper = new ObjectMapper();	
			 	EndpointDefinition endPointDefinition = objectMapper.readValue(jsonbody, EndpointDefinition.class);  

			 	ExportManifest exportManifest = new ExportManifest();
				exportManifest.setAuthorizedBy(user);
				exportManifest.setEndpointDefinition(endPointDefinition);
				exportManifest.setProjectId(projectId);
			 	String exportHandler = endPointDefinition.getExportHandler();
				
				ExportRequest request = new ExportRequest(exportManifest);
				//TODO - Need to extract the credentails
				XDAT.sendJmsRequest(request);
				return  new ResponseEntity<>("Export Request for Project "+  projectId + " to  "+ exportHandler +" has been queued", HttpStatus.OK);
			}catch(ExporterNotFoundException enfe) {
				return  new ResponseEntity<>("No exporter found ", HttpStatus.BAD_REQUEST);
			}catch(Exception enfe) {
				return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
	    }

	    
	    @ApiOperation(value = "Export given list of datatypes")
	    @ApiResponses({
	            @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "dryrun/projects/{projectId}", method = POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	    public ResponseEntity<String> dryrun(@PathVariable("projectId") String projectId,final @RequestBody String jsonbody) {
	       
			final UserI user = getSessionUser();

			try {
				ObjectMapper objectMapper = new ObjectMapper();	
			 	EndpointDefinition endPointDefinition = objectMapper.readValue(jsonbody, EndpointDefinition.class);  
				String exportHandler = endPointDefinition.getExportHandler();
				
				//Does the user have the rights on the Project to be able to export?
				HttpStatus status = canDeleteProject(projectId);
				if (status != null) {
		            return new ResponseEntity<>("User does not have permissions" ,status);
		        }
				
				ExportManifest exportManifest = new ExportManifest();
				exportManifest.setAuthorizedBy(user);
				exportManifest.setEndpointDefinition(endPointDefinition);
				exportManifest.setProjectId(projectId);
				//TODO - Need to extract the credentails
				SiteCredentials crendentials = new SiteCredentials("admin","admin");
				exportManifest.setCredentials(crendentials);
				
				
				ExporterI exporter = _exportManager.getExporterByExportHandlerAnnotation(exportHandler);
				TransportManifest transportManifest = exporter.dryrun(exportManifest, user);
				String serialized = new ObjectMapper().writeValueAsString(transportManifest);
				return  new ResponseEntity<>(serialized, HttpStatus.OK);
			}catch(ExporterNotFoundException enfe) {
				return  new ResponseEntity<>("No export handler found ", HttpStatus.BAD_REQUEST);
			}catch(Exception enfe) {
				return  new ResponseEntity<>( HttpStatus.BAD_REQUEST);
			}
	    }

		private final ConfigService              _configService;
		private final SerializerService          _serializerService;
	    
}
