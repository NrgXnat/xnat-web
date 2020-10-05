package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;

import org.json.JSONObject;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ProjSubExptListService;
import org.restlet.resource.Representation;
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

@Api("XNAT Experiment details API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjSubExptListXapi extends AbstractXapiProjectRestController{

	public ProjSubExptListXapi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProjSubExptListService projSubExptListService) {
        super(userManagementService, roleHolder);
        _projSubExptListService = projSubExptListService;
    }
	
	@ApiOperation(value = "Get the resources for a project, subject, or experiment", notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources or /projects/XNAT_01/subjects/XNAT_01_01/resources.", response = XnatAbstractresourceI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
			@ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = {
			"/projects/{projectId}/experiments",
			"/projects/{projectId}/experiments/{experimentId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<String> getResources(
			@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			 @ApiParam(value = "The ID of the experimentId.") @PathVariable(required = false) final String experimentId
			) throws Exception {
		log.debug("projects experminet Controller Api-");
		 String projectExperiment= _projSubExptListService.getProjectExperiments(getSessionUser(),projectId, experimentId);
		 if (projectExperiment == null) {
				throw new NotFoundException("No Project with experiment was found.");
			}
			return new ResponseEntity<>(projectExperiment, HttpStatus.OK);
	    }
	 
	 private final ProjSubExptListService _projSubExptListService;
}
