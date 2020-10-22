package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ProjectSubjectListService;
import org.nrg.xnat.services.resources.files.FileListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Api("XNAT File Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class FileResourceXapi extends AbstractXapiProjectRestController {
	private static final Logger _log = LoggerFactory.getLogger(FileResourceXapi.class);
	
	@Autowired
	public FileResourceXapi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final FileListService fileListService) {
		super(userManagementService, roleHolder);
		_fileListService = fileListService;
	}

	@ApiOperation(value = "Get the resources for a project, subject, or experiment", notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources or /projects/XNAT_01/subjects/XNAT_01_01/resources.", response = XnatAbstractresourceI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
			@ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = {
			"/experiments/{assessedId}/scans/{scanId}/files",
			"/experiments/{assessedId}/scans/{scanId}/resources/{resourceId}/files" }, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<String> getResourcefiles(
			@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String assessedId,
			 @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String scanId
			)
					
			throws NotFoundException, IOException {
		_log.debug("Controller Api- ger ResourceFiles by project id or subject id");
		final String projectSubject = _fileListService.getResourceFiles(getSessionUser(),assessedId, scanId);
	    if (projectSubject == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(projectSubject, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get the resources for a project, subject, or experiment", notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources or /projects/XNAT_01/subjects/XNAT_01_01/resources.", response = XnatAbstractresourceI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
			@ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = {
			/* "/experiments/{assessedId}/scans/{scanId}/resources", */
			"/experiments/{assessedId}/scans/{scanId}/resources/{resourceId}" }, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<String> getResources(@ApiParam(value = "The ID of the assessedId.") @PathVariable(required = false) final String assessedId,
			 @ApiParam(value = "The ID of the scanId.") @PathVariable(required = false) final String scanId )
					
			throws NotFoundException, IOException {
		_log.debug("Controller Api- ger Resources by resource id");
		final String resource = _fileListService.getResources(getSessionUser(),assessedId, scanId);
	    if (resource == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}
	
	private final FileListService _fileListService;
}
