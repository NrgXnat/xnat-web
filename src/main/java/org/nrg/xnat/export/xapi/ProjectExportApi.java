package org.nrg.xnat.export.xapi;

import static org.nrg.xdat.security.helpers.AccessLevel.Admin;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;
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
import org.nrg.xnat.tracking.entities.EventTrackingData;
import org.nrg.xnat.tracking.services.EventTrackingDataHibernateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(value = "/export")
@Api("Export API")
@Slf4j
/*
 * Class which handles REST calls to export
 */

public class ProjectExportApi extends AbstractXapiProjectRestController {
	    
	    private final ExportManagerI _exportManager;
		private final ConfigService              _configService;
		private final SerializerService          _serializerService;
		private final EventTrackingDataHibernateService	 _eventTrackingDataHibernateService;

	    @Autowired
	    public ProjectExportApi(final ExportManagerI exportManager,
	                     final UserManagementServiceI userManagementService,
	                     final RoleHolder roleHolder,
	                     final ConfigService configService,
	                     final SerializerService serializerService,
	                     final EventTrackingDataHibernateService eventTrackingDataHibernateService
	                     ) {
	        super(userManagementService, roleHolder);
	        this._exportManager = exportManager;
	        this._configService = configService;
	        this._serializerService = serializerService;
	        this._eventTrackingDataHibernateService = eventTrackingDataHibernateService;
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
			 	_configService.replaceConfig(user.getUsername(), "Export Endpoint Added to Project", ExportConstants.TOOL_ID, endPointDefinition.getLabel(),true, jsonbody, Scope.Project, projectId);
			 }catch(Exception e) {
				   log.error("Possibly invalid json ", e);
		           return new ResponseEntity<>("Probably incorrect JSON",HttpStatus.BAD_REQUEST);
			 }
			 
			return  new ResponseEntity<>("Destination added", HttpStatus.OK);
	    }

	    @ApiOperation(value = "List Export Endpoint configured for a project")
	    @ApiResponses({
	    	    @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "/endpoint/list/{projectId}",  method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<List<Configuration>> list(@PathVariable("projectId") String projectId) {
			 try {
					HttpStatus status = canDeleteProject(projectId);
					if (status != null) {
			            return new ResponseEntity<>(null,status);
			        }

					List<Configuration> configs = _configService.getConfigsByTool(ExportConstants.TOOL_ID, Scope.Project, projectId);
					if (configs != null && configs.size() > 0)
						return  new ResponseEntity<>(configs, HttpStatus.OK);
					else {
						List<Configuration> emptyOne = new ArrayList<Configuration>();
						return  new ResponseEntity<>(emptyOne, HttpStatus.OK);
					}
			 }catch(Exception e) {
				   log.error("Possibly invalid json ", e);
		           return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			 }
	    }

	    @ApiOperation(value = "Get Export Endpoint configured for a project for a specific label")
	    @ApiResponses({
	    	    @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "/endpoint/get/{projectId}",  method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<Configuration> getProjectSpecificEndpoint(@PathVariable("projectId") String projectId,@RequestParam(value = "label", required=true) final String label) {
			 try {
					HttpStatus status = canDeleteProject(projectId);
					if (status != null) {
			            return new ResponseEntity<>(null,status);
			        }

					List<Configuration> configs = _configService.getConfigsByTool(ExportConstants.TOOL_ID, Scope.Project, projectId);
					boolean found = false;
					if (configs != null && configs.size() > 0) {
						for (Configuration c : configs) {
							if (label.equals(c.getPath())) {
								return  new ResponseEntity<>(c, HttpStatus.OK);
							}
						}
						return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
					}else
						return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			 }catch(Exception e) {
				   log.error("Possibly invalid json ", e);
		           return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			 }
	    }

	    @ApiOperation(value = "Enable a project export endpoint")
	    @ApiResponses({
	            @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "/endpoint/enable/{projectId}", method = POST,  restrictTo = Admin)
	    public ResponseEntity<Configuration> enable(@PathVariable("projectId") String projectId, @RequestParam(value = "label", required=true) final String label, @RequestParam(value = "enabled", required=true) final String enabled) {
			final UserI user = getSessionUser();
			 Configuration configurationForToolAndExportHandler = _configService.getConfig(ExportConstants.TOOL_ID,label, Scope.Project, projectId);
			 boolean enabledBool = Boolean.parseBoolean(enabled);
			 if (configurationForToolAndExportHandler != null) {
				 try {
					 if (enabledBool)
						 _configService.enable(user.getUsername(), "User enabled", ExportConstants.TOOL_ID, label, Scope.Project, projectId);
					 else 
						 _configService.disable(user.getUsername(), "User disabled", ExportConstants.TOOL_ID, label, Scope.Project, projectId);
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

	    
	    @ApiOperation(value = "Export a project as per provided endpoint definition")
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
				objectMapper.setSerializationInclusion(Include.NON_NULL);
			 	EndpointDefinition endPointDefinition = objectMapper.readValue(jsonbody, EndpointDefinition.class);  

			 	ExportManifest exportManifest = new ExportManifest();
				exportManifest.setAuthorizedBy(user);
				exportManifest.setEndpointDefinition(endPointDefinition);
				exportManifest.setProjectId(projectId);
			 	String label = endPointDefinition.getLabel();
				
				ExportRequest request = new ExportRequest(exportManifest);
				XDAT.sendJmsRequest(request);
				return  new ResponseEntity<>("Export Request for Project "+  projectId + " to  "+ label +" has been queued", HttpStatus.OK);
			}catch(ExporterNotFoundException enfe) {
				return  new ResponseEntity<>("No exporter found ", HttpStatus.BAD_REQUEST);
			}catch(Exception enfe) {
				return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
	    }

	    
	    @ApiOperation(value = "Dryrun a project as per the provided endpoint definition")
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
	    
	    
	    @ApiOperation(value = "Verify Connection to Export Endpoint")
	    @ApiResponses({
	            @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "endpoint/verify", method = POST,  produces = MediaType.TEXT_PLAIN_VALUE)
	    public ResponseEntity<String> verify(@RequestParam(value = "destination", required=true) final String destination, @RequestParam(value = "username", required=true) final String username, @RequestParam(value = "password", required=true) final String password) {
	    	try {
		    	int rCode = testExportDestination(destination, username, password);
		    	if (rCode == 200) {
					return  new ResponseEntity<>("Passed", HttpStatus.OK);
				}else 
					return  new ResponseEntity<>("Failed", HttpStatus.FORBIDDEN);
	    	}catch(IOException ioe) {
				return  new ResponseEntity<>("Connection Failed. Is the Site " + destination+ " accessible", HttpStatus.BAD_REQUEST);
	    	}
	    }

	    @ApiOperation(value = "Get History for the project ")
	    @ApiResponses({
	    	    @ApiResponse(code = 200, message = "Success"),
	            @ApiResponse(code = 400, message = "Invalid parameters"),
	            @ApiResponse(code = 500, message = "Unexpected error")})
	    @XapiRequestMapping(value = "/history/projects/{projectId}",  method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<List<EventTrackingData>> getHistory(@PathVariable("projectId") String projectId) {
			 try {
					HttpStatus status = canDeleteProject(projectId);
					if (status != null) {
			            return new ResponseEntity<>(null,status);
			        }
					List<EventTrackingData> required = new ArrayList<EventTrackingData>();
					List<EventTrackingData> events = _eventTrackingDataHibernateService.getAll();
					for (EventTrackingData e : events) {
						if (e.getKey().startsWith(ExportConstants.EXPORT_TRACKING_KEY_PREFIX +"/" + projectId)) {
							required.add(e);
						}
					}
					Comparator<EventTrackingData> compareByDate = new Comparator<EventTrackingData>() {
					    @Override
					    public int compare(EventTrackingData o1, EventTrackingData o2) {
					        return o1.getCreated().compareTo(o2.getCreated());
					    }
					};
					if (required != null && required.size() > 0) {
						Collections.sort(required, compareByDate.reversed());
						return  new ResponseEntity<>(required, HttpStatus.OK);
					}else
						return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			 }catch(Exception e) {
				   log.error("Possibly invalid json ", e);
		           return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
			 }
	    }
	    
	    private int testExportDestination(String requestURL,  String username, String password)  throws IOException {
			int rCode = 500;
	        String charset = "UTF-8";

            String uQuery = String.format("username=%s", URLEncoder.encode(username, charset));
            String pQuery = String.format("password=%s", URLEncoder.encode(password, charset));
	        URL url = new URL(requestURL+"/login/ajax?" + uQuery +"&" + pQuery);
           
	        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        httpConn.setUseCaches(false);
	        httpConn.setRequestProperty("User-Agent", "XNAT Export Agent");

			try {
				httpConn.connect();
                rCode = httpConn.getResponseCode();
			}catch(IOException ioe) {
				log.debug("Could not establish connection");
			}finally{
				httpConn.disconnect();
			}
			return rCode;
	    }

	    
	    
}
