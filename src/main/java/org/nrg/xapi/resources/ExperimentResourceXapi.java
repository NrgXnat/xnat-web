package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ExperimentResourceService;
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

@Api("XNAT Experiment File Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ExperimentResourceXapi extends AbstractXapiProjectRestController {

	@Autowired
	public ExperimentResourceXapi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final ExperimentResourceService experimentService) {
		super(userManagementService, roleHolder);
		_experimentService = experimentService;
	}

	@ApiOperation(value = "Get the resources for a experiment or resource", notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources or /projects/XNAT_01/subjects/XNAT_01_01/resources.", response = XnatAbstractresourceI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
			@ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = { "/experiments/{experimentId}/files", "/experiments/{experimentId}/resources",
			"/experiments/{experimentId}/resources/{resourceId}/files" }, 
			produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<String> getResources(HttpServletRequest request,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final String resourceId)
			throws NotFoundException, IOException {
		if (request != null) {
			System.out.println("request1 --- " + request);
		}
		System.out.println("Controller Api called");
		final String projectSubject = _experimentService.getExperimentResource(experimentId, resourceId);
		if (projectSubject == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(projectSubject, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get the resources for a experiment or resource", notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources or /projects/XNAT_01/subjects/XNAT_01_01/resources.", response = XnatAbstractresourceI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
			@ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = { "/experiments/{experimentId}/resources/{resourceId}" }, produces = {MediaType.APPLICATION_XML_VALUE}, method = GET)
	public ResponseEntity<String> getResourcesXml(HttpServletRequest request,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final String resourceId)
			throws NotFoundException, IOException {
		if (request != null) {
			System.out.println("request2 --- " + request);
		}
		System.out.println("Controller2 Api called");
		final String projectSubject = _experimentService.getExperimentResource(experimentId, resourceId);
		if (projectSubject == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(projectSubject, HttpStatus.OK);
	}

	private final ExperimentResourceService _experimentService;
}
