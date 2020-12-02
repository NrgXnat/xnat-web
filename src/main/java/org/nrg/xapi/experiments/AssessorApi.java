package org.nrg.xapi.experiments;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.experiments.AssessorService;
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


@Api("XNAT Assessor Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class AssessorApi extends AbstractXapiProjectRestController {

	@Autowired
	public AssessorApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final AssessorService assessorService) {
		super(userManagementService, roleHolder);
		_assessorService = assessorService;
	}
	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatImageassessordata>> getAssessorListByProjectAndSubjectAndExperiment(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId) throws Exception {
		log.debug("Controller Api- get xnatImageassessordatas");
		List<XnatImageassessordata> xnatImageassessordatas = _assessorService.findByProjectAndSubjectAndExperiment(getSessionUser(), projectId, subjectId,experimentId );
		if (xnatImageassessordatas == null) {
			throw new NotFoundException("No xnatImageassessordatas with data was found.");
		}
		return new ResponseEntity<>(xnatImageassessordatas, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatImageassessordata> getAssessorByIdAndProjectAndSubjectAndExperimentAndAssessor(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId) throws Exception {
		log.debug("Controller Api- get xnatImageassessordatas");
		XnatImageassessordata xnatImageassessordata = _assessorService.findByIdAndProjectAndSubjectAndExperiment(getSessionUser(), projectId, subjectId,experimentId, assessorId);
		if (xnatImageassessordata == null) {
			throw new NotFoundException("No xnatImageassessordatas with data was found.");
		}
		return new ResponseEntity<>(xnatImageassessordata, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Get list of assessors", notes = "The experiments function returns a list of all assessors configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured assessors."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatImageassessordata>> getByExperiment(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId) throws Exception {
		log.debug("Controller Api- get xnatImageassessordatas");
		List<XnatImageassessordata> xnatImageassessordatas = _assessorService.findByExperiment(getSessionUser(), experimentId);
		if (xnatImageassessordatas == null) {
			throw new NotFoundException("No xnatImageassessordatas with data was found.");
		}
		return new ResponseEntity<>(xnatImageassessordatas, HttpStatus.OK);
	}
	
	private final AssessorService _assessorService;
}
