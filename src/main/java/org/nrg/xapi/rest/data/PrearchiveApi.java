package org.nrg.xapi.rest.data;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xapi.model.PrearcSessionResource;
import org.nrg.xapi.model.PrearcSessionScan;
import org.nrg.xapi.model.PrearcSessionScanResFile;
import org.nrg.xapi.model.Prearchive;
import org.nrg.xnat.helpers.prearchive.SessionException;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.services.prearchive.PrearchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Prearchiv Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class PrearchiveApi extends AbstractXapiProjectRestController {
	
    @Autowired
    public PrearchiveApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final PrearchiveService prearchiveService) {
        super(userManagementService, roleHolder);
        _prearchiveService = prearchiveService;
    }
    
    @ApiOperation(value = "Gets the requested  Prearchive", notes = "Returns the  Prearchive with the specified PROJECT ID", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested Prearchive wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = {"/prearchive","/prearchive/projects/{projectId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<Prearchive> getAllPrearchives(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                              @ApiParam(value = "The value of the tag.") @RequestParam(name = "tag", required = false) final String tag) throws SQLException, SessionException, Exception {
		log.debug("User {} requested Prearchive", getSessionUser().getUsername());
		return _prearchiveService.findAllPrearchives(getSessionUser(), projectId, tag);
	}
    
    
    @ApiOperation(value = "Gets the requested  Prearchive session resource", notes = "Returns the  Prearchive session resource", response = List.class, responseContainer = "list")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested Prearchive session resource wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = {"/prearchive/projects/{projectId}/{sessionTimestamp}/{sessionLabel}/resources"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<PrearcSessionResource> getAllPrearcSessionResources(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
																	@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionTimestamp,
																	@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionLabel) throws ActionException {
		log.debug("User {} requested Prearchive session resource", getSessionUser().getUsername());
		return _prearchiveService.findAllPrearcSessionResource(getSessionUser(), projectId, sessionTimestamp, sessionLabel);
	}
    
    @ApiOperation(value = "Gets the requested  Prearchive session scans", notes = "Returns the  Prearchive session scans", response = List.class, responseContainer = "list")
   	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
   			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
   			@ApiResponse(code = 404, message = "The requested Prearchive session scans wasn't found."),
   			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
   	@XapiRequestMapping(value = {"/prearchive/projects/{projectId}/{sessionTimestamp}/{sessionLabel}/scans"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
   	public List<PrearcSessionScan> getAllPrearcSessionScans(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
															@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionTimestamp,
															@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionLabel) throws ActionException {
   		log.debug("User {} requested Prearchive session resource", getSessionUser().getUsername());
   		return _prearchiveService.findAllPrearcSessionScans(getSessionUser(), projectId, sessionTimestamp, sessionLabel);
   	}
    
    @ApiOperation(value = "Gets the requested  Prearchive session resource with scanId", notes = "Returns the  Prearchive session resource with the specified SCAN_ID", response = List.class, responseContainer = "list")
   	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
   			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
   			@ApiResponse(code = 404, message = "The requested Prearchive session resource wasn't found."),
   			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
   	@XapiRequestMapping(value = {"/prearchive/projects/{projectId}/{sessionTimestamp}/{sessionLabel}/scans/{scanId}/resources"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
   	public List<PrearcSessionResource> getAllPrearcSessionResourcesByScanId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
																			@ApiParam(value = "The ID of the scan.") @PathVariable  final Integer scanId,
																			@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionTimestamp,
																			@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionLabel) throws ActionException, NotFoundException {
   		log.debug("User {} requested Prearchive session resource with scan ID", getSessionUser().getUsername(), scanId);
   		return _prearchiveService.findAllPrearcSessionResourceByScanId(getSessionUser(), projectId, sessionTimestamp, sessionLabel,scanId);
   	}
    
    @ApiOperation(value = "Gets the requested  Prearchive session resource with scanId", notes = "Returns the  Prearchive session resource with the specified SCAN_ID", response = List.class, responseContainer = "list")
   	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
   			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
   			@ApiResponse(code = 404, message = "The requested Prearchive session resource wasn't found."),
   			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
   	@XapiRequestMapping(value = {"/prearchive/projects/{projectId}/{sessionTimestamp}/{sessionLabel}/scans/{scanId}/resources/{resourceId}/files"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
   	public List<PrearcSessionScanResFile> getAllPrearcSessionResourcesByScanIdAndResourceId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
																							@ApiParam(value = "The ID of the scan.") @PathVariable  final Integer scanId,
																							@ApiParam(value = "The ID of the resource.") @PathVariable  final String resourceId,
																							@ApiParam(value = "The valur of the filepath.") @RequestParam(required = false)  final String filepath,
																							@ApiParam(value = "The valur of the prettyPrint.") @RequestParam(defaultValue = "false")  final boolean prettyPrint,
																							@ApiParam(value = "The valur of the filepath.")   final HttpServletRequest request,
																							@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionTimestamp,
																							@ApiParam(value = "The value of the timestamp.") @PathVariable  final String sessionLabel) throws ActionException, NotFoundException, DataFormatException {
   		log.debug("User {} requested Prearchive session resource with scan ID", getSessionUser().getUsername(), scanId);
   		return _prearchiveService.findAllPrearcSessionResourceByScanIdAndResourceId(getSessionUser(), projectId, sessionTimestamp, sessionLabel,scanId, resourceId, filepath, prettyPrint, request);
   	}
    
    @ApiOperation(value = "Create a new prearchive rebuild", notes = "Creates the submitted rebuild.", response = XnatProjectdata.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the newly created project."),
			@ApiResponse(code = 400, message = "The requested prearchive rebuild wasn't found."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to create prearchive rebuild"),
			@ApiResponse(code = 404, message = "The specified prearchive rebuild doesn't exist"),
			@ApiResponse(code = 409, message = "The specified prearchive rebuild already exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/services/prearchive/rebuild",   consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
						produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, method = POST)
	public Prearchive createPrearchiveRebuild(@ApiParam("The value to src")  @RequestParam(name = "src") List<String> src,
                                              @ApiParam("The value to overrideLock") @RequestParam(name = "overrideLock") boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException, DataFormatException{
		log.debug("User {} requested to create prearchive rebuild  with src {}", getSessionUser().getUsername(), src);
		return _prearchiveService.createPrarchiveRebuild(getSessionUser(), src, overrideLock);
	}
    
    @ApiOperation(value = "Create a new prearchive delete", notes = "Creates the submitted delete.", response = XnatProjectdata.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the newly created project."),
			@ApiResponse(code = 400, message = "The requested prearchive rebuild wasn't found."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to create prearchive delete"),
			@ApiResponse(code = 404, message = "The specified prearchive delete doesn't exist"),
			@ApiResponse(code = 409, message = "The specified prearchive delete already exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/services/prearchive/delete",   consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
						produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, method = POST)
	public Prearchive deletePrearchive(@ApiParam("The value to src")  @RequestParam(name = "src") List<String> src,
                                       @ApiParam("The value to overrideLock") @RequestParam(name = "overrideLock") boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException, DataFormatException{
		log.debug("User {} requested to create prearchive rebuild  with src {}", getSessionUser().getUsername(), src);
		return _prearchiveService.deletePrarchive(getSessionUser(), src, overrideLock);
	}
    
    @ApiOperation(value = "Create a new prearchive delete", notes = "Creates the submitted delete.", response = XnatProjectdata.class)
   	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the newly created project."),
   			@ApiResponse(code = 400, message = "The requested prearchive rebuild wasn't found."),
   			@ApiResponse(code = 403, message = "The user doesn't have permission to create prearchive delete"),
   			@ApiResponse(code = 404, message = "The specified prearchive delete doesn't exist"),
   			@ApiResponse(code = 409, message = "The specified prearchive delete already exist"),
   			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
   	@XapiRequestMapping(value = "/services/prearchive/move",   consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
   						produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, method = POST)
   	public Prearchive movePrearchive(@ApiParam("The value to src")  @RequestParam(name = "src") List<String> src,
                                     @ApiParam("The value to overrideLock") @RequestParam(name = "newProject") String newProject) throws InitializationException, InsufficientPrivilegesException, NotFoundException, DataFormatException, ResourceAlreadyExistsException{
   		log.debug("User {} requested to create prearchive rebuild  with src {}", getSessionUser().getUsername(), src);
   		return _prearchiveService.movePrarchive(getSessionUser(), src, newProject);
   	}
    
    @ApiOperation(value = "Create a new resource file", notes = "Creates the submitted resource file.", response = Integer.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
    @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
    @ApiResponse(code = 404, message = "The specified project doesn't exist"),
    @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	@XapiRequestMapping(value = "/services/import",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
	public List<String> importFile(@ApiParam("The resource file to be created.")  @RequestParam MultipartFile file, HttpServletRequest request) throws IOException, DataFormatException, ServerException, ClientException, NotFoundException {
		XnatResourceInfo xnatResourceInfo = getXnatResourceInfo(file);
		log.debug("User {} requested to import file", getSessionUser().getUsername());
		return _prearchiveService.importFiles(getSessionUser(), request, xnatResourceInfo);
	}
	
	private XnatResourceInfo getXnatResourceInfo(MultipartFile file) throws IOException {
		return XnatResourceInfo.builder()
							   .username(getSessionUser().getUsername())
							   .created(new Date())
							   .name(file.getOriginalFilename())
							   .fileSize(file.getSize())
							   .multipartFile(file).build();
	}
   private final PrearchiveService _prearchiveService;
}
