package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.subjects.XnatExperiment;
import org.nrg.xapi.model.subjects.XnatProject;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ExperimentListService;
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
public class ExperimentListApi extends AbstractXapiProjectRestController {

	@Autowired
	public ExperimentListApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final ExperimentListService experimentListService) {
		super(userManagementService, roleHolder);
		_experimentListService = experimentListService;
	}

	@ApiOperation(value = "Gets the requested  experiment", notes = "Returns the  experiment with the specified ID", response = XnatProject.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 404, message = "The requested project wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatExperiment> getExperimentById(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)
			throws Exception {
		log.debug("Controller Api- get Resources by projectId");
		XnatExperiment xnatExperiment = _experimentListService.findById(getSessionUser(), experimentId);
		if (xnatExperiment == null) {
			throw new NotFoundException("No experiment with ID was found.");
		}
		return new ResponseEntity<>(xnatExperiment, HttpStatus.OK);
	}

	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all subjects configured in the XNAT system.", response = XnatProject.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperiment>> getExperimentList() throws Exception {
		log.debug("Controller Api- get xnatExperiments");
		List<XnatExperiment> xnatExperiments = _experimentListService.getAll(getSessionUser());
		if (xnatExperiments == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatExperiments, HttpStatus.OK);
	}

	private final ExperimentListService _experimentListService;

}
