package org.nrg.xapi.files;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT File Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class FileApi extends AbstractXapiProjectRestController {

	@Autowired
	public FileApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final FileService fileService) {
		super(userManagementService, roleHolder);
		_fileService = fileService;
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByProject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId)throws Exception {
		log.debug("Controller Api- get files by  projectId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByProject(getSessionUser(), projectId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getBySubject(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get files by  subjectId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findBySubject(getSessionUser(), subjectId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByProjectAndSubject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get files by  projectId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByProjectAndSubject(getSessionUser(), projectId, subjectId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByProjectAndResource(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)throws Exception {
		log.debug("Controller Api- get files by  projectId And resourceId ");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByProjectAndResource(getSessionUser(), projectId, resourceId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getBySubjectAndResource(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)throws Exception {
		log.debug("Controller Api- get files by  subjectId And resourceId ");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findBySubjectAndResource(getSessionUser(), subjectId, resourceId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	private final FileService _fileService;
}
