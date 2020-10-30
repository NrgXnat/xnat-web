package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.subjects.XnatExperiment;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ExperimentService;
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
public class ExperimentApi extends AbstractXapiProjectRestController {

	@Autowired
	public ExperimentApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final ExperimentService experimentService) {
		super(userManagementService, roleHolder);
		_experimentService = experimentService;
	}

	@ApiOperation(value = "Gets the requested  experiment", notes = "Returns the  experiment with the specified ID", response = XnatExperiment.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 404, message = "The requested project wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatExperimentdata> getExperimentById(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)
			throws Exception {
		log.debug("Controller Api- get Resources by projectId");
		XnatExperimentdata xnatExperiment = _experimentService.findById(getSessionUser(), experimentId);
		if (xnatExperiment == null) {
			throw new NotFoundException("No experiment with ID was found.");
		}
		return new ResponseEntity<>(xnatExperiment, HttpStatus.OK);
	}

	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all subjects configured in the XNAT system.", response = XnatExperiment.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperimentdata>> getExperimentList() throws Exception {
		log.debug("Controller Api- get xnatExperiments");
		List<XnatExperimentdata> xnatExperiments = _experimentService.getAll(getSessionUser());
		if (xnatExperiments == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatExperiments, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  experiment", notes = "Returns the  experiment with the specified ID", response = XnatExperiment.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 404, message = "The requested project wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/new/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatExperimentdata> getExperimentByExperimentId(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)
			throws Exception {
		log.debug("Controller Api- get Resources by projectId");
		XnatExperimentdata xnatExperiment = _experimentService.findByExperimentId(getSessionUser(), experimentId);
		if (xnatExperiment == null) {
			throw new NotFoundException("No experiment with ID was found.");
		}
		return new ResponseEntity<>(xnatExperiment, HttpStatus.OK);
	}

	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all subjects configured in the XNAT system.", response = XnatExperiment.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/new/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperimentdata>> getAllExperimentList() throws Exception {
		log.debug("Controller Api- get xnatExperiments");
		List<XnatExperimentdata> xnatExperiments = _experimentService.getAllExperiments(getSessionUser());
		if (xnatExperiments == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatExperiments, HttpStatus.OK);
	}



	private final ExperimentService _experimentService;

}
