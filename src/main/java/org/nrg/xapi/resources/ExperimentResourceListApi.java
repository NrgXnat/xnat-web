package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.subjects.XnatExperimentResource;
import org.nrg.xapi.model.subjects.XnatProject;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ExperimentResourceListService;
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

@Api("XNAT experiment Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ExperimentResourceListApi extends AbstractXapiProjectRestController {

	@Autowired
	public ExperimentResourceListApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final ExperimentResourceListService experimentResourceListService) {
		super(userManagementService, roleHolder);
		_experimentResourceListService = experimentResourceListService;
	}

	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentResource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperimentResource>> getExperimentList(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId) throws Exception {
		log.debug("Controller Api- get xnatExperiments");
		List<XnatExperimentResource> xnatExperimentResource = _experimentResourceListService.findResourceByExperimentId(getSessionUser(), experimentId);
		if (xnatExperimentResource == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatExperimentResource, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of experiment scan Resources", notes = "The experiments function returns a list of all experiment scan Resources configured in the XNAT system.", response = XnatExperimentResource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperimentResource>> getExperimentScanResourceList(@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId,
			@ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId) throws Exception {
		log.debug("Controller Api- get xnatExperiments");
		List<XnatExperimentResource> xnatExperimentScanResource = _experimentResourceListService.findExperimentScanResourcesByAssessedIdAndScanId(getSessionUser(), assessedId, scanId);
		if (xnatExperimentScanResource == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatExperimentScanResource, HttpStatus.OK);
	}

	private final ExperimentResourceListService _experimentResourceListService;
}
