package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ResourceService;
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

@Api("XNAT Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ResourceApi extends AbstractXapiProjectRestController {

	@Autowired
	public ResourceApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ResourceService resourceService) {
		super(userManagementService, roleHolder);
		_resourceService = resourceService;
	}
	
	@ApiOperation(value = "Gets the requested  resources", notes = "Returns the  resources with the specified Experiment ID", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resources."),
			@ApiResponse(code = 404, message = "The requested resources wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceByExperimentId(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)
			throws Exception {
		log.debug("Controller Api- get resources by experimentId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findByExperimentId(getSessionUser(), experimentId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resources with experimentId was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public ResponseEntity<XnatAbstractresource> getResourceByIdAndExperimentId(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)
			throws Exception {
		log.debug("Controller Api- get resources by Id and experimentId");
		XnatAbstractresource xnatAbstractresource = _resourceService.findByIdAndExperimentId(getSessionUser(), resourceId, experimentId);
		if (xnatAbstractresource == null) {
			throw new NotFoundException("No resource with ID and experimentId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresource, HttpStatus.OK);
	}
	

	private final ResourceService _resourceService;

}
