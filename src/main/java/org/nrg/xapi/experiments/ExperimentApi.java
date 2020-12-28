package org.nrg.xapi.experiments;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.experiments.ExperimentService;
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
	public ExperimentApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ExperimentService experimentService) {
		super(userManagementService, roleHolder);
		_experimentService = experimentService;
	}

	@ApiOperation(value = "Gets the requested  experiment", notes = "Returns the  experiment with the specified ID", response = XnatExperimentdata.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested experiment."),
			@ApiResponse(code = 404, message = "The requested experiment wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatExperimentdata> getExperimentById(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)
			throws Exception {
		log.debug("Controller Api- get experiment by experimentId");
		XnatExperimentdata xnatExperiment = _experimentService.findById(getSessionUser(), experimentId);
		if (xnatExperiment == null) {
			throw new NotFoundException("No experiment with ID was found.");
		}
		return new ResponseEntity<>(xnatExperiment, HttpStatus.OK);
	}

	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperimentdata>> getExperimentList() throws Exception {
		log.debug("Controller Api- get experiments");
		List<XnatExperimentdata> xnatExperiments = _experimentService.getAll(getSessionUser());
		if (xnatExperiments == null) {
			throw new NotFoundException("No experiments with data was found.");
		}
		return new ResponseEntity<>(xnatExperiments, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperimentdata>> getProjectExperimentList(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get experiments");
		List<XnatExperimentdata> xnatExperiments = _experimentService.findByProject(getSessionUser(), projectId);
		if (xnatExperiments == null) {
			throw new NotFoundException("No experiments with data was found.");
		}
		return new ResponseEntity<>(xnatExperiments, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Get single experiment", notes = "The experiments function returns a single experiment configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/experiments/{experimentId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatExperimentdata> getExperimentByIdAndProject(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get experiments");
		XnatExperimentdata xnatExperiment = _experimentService.findByIdAndProject(getSessionUser(), experimentId, projectId);
		if (xnatExperiment == null) {
			throw new NotFoundException("No experiments with data was found.");
		}
		return new ResponseEntity<>(xnatExperiment, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatExperimentdata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatExperimentdata>> getProjectSubjectExperimentList(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId, @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId) throws Exception {
		log.debug("Controller Api- get experiments");
		List<XnatExperimentdata> xnatExperiments = _experimentService.findByProjectAndSubject(getSessionUser(), projectId, subjectId);
		if (xnatExperiments == null) {
			throw new NotFoundException("No experiments with data was found.");
		}
		return new ResponseEntity<>(xnatExperiments, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Delete an existing experiment", notes = "Deletes the specified experiment.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified experiment."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete experiments in the specified experiment"),
                   @ApiResponse(code = 404, message = "The specified experiment or experiment doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/experiments/{experimentId}","/experiments/{experimentId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteProject(@ApiParam("The ID of the experiment to be deleted") @PathVariable(required = false) final String projectId,
    		@ApiParam("The ID of the experiment to be deleted") @PathVariable(required = false) final String experimentId) throws Exception {
        log.debug("Controller Api- Delete experiment {}", projectId);
        _experimentService.deleteById(getSessionUser(), experimentId, projectId);
    }
	

	private final ExperimentService _experimentService;

}
