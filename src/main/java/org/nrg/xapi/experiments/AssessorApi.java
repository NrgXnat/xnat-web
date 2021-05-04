package org.nrg.xapi.experiments;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.experiments.AssessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId  wasn't found."),
					@ApiResponse(code = 404, message = "The requested assessors wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatImageassessordata> getAllByProjectIdAndSubjectIdAndExperimentId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested assessor with project ID {} , with subject ID {} and with experiment ID {} }", getSessionUser().getUsername(), projectId, subjectId, experimentId);
		return _assessorService.findAllByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId,experimentId );
	}
	
	@ApiOperation(value = "Get list of experiments", notes = "The experiments function returns a list of all experiments configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured experiments."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or assessorId  wasn't found."),
					@ApiResponse(code = 404, message = "The requested assessor wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XnatImageassessordata getByIdAndProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
			@ApiParam(value = "The ID of the assessor.") @PathVariable final String assessorId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested assessor with project ID {} , with subject ID {} , with experiment ID {} and with assessor ID {}  }", getSessionUser().getUsername(), projectId, subjectId, experimentId, assessorId);
		return _assessorService.findByIdAndProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId,experimentId, assessorId).orElseThrow(() -> new NotFoundException(XnatImageassessordata.SCHEMA_ELEMENT_NAME, assessorId));
	}
	
	
	@ApiOperation(value = "Get list of assessors", notes = "The experiments function returns a list of all assessors configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured assessors."),
					@ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
					@ApiResponse(code = 404, message = "The requested assessors wasn't found."),		
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatImageassessordata> getAllByExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested assessor with experiment ID {} }", getSessionUser().getUsername(), experimentId);
		return _assessorService.findAllByExperimentId(getSessionUser(), experimentId);
	}
	
	@ApiOperation(value = "Get list of assessors", notes = "The experiments function returns a list of all assessors configured in the XNAT system.", response = XnatImageassessordata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured assessors."),
					@ApiResponse(code = 400, message = "The requested either assessorId or experimentId wasn't found."),
					@ApiResponse(code = 404, message = "The requested assessor wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XnatImageassessordata getByAssessorIdAndExperimentId(@ApiParam(value = "The ID of the assessor.") @PathVariable final String assessorId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested assessor with experiment ID {} and with assessor ID {}  }", getSessionUser().getUsername(), experimentId, assessorId);
		return _assessorService.findByIdAndExperimentId(getSessionUser(), assessorId, experimentId).orElseThrow(() -> new NotFoundException(XnatImageassessordata.SCHEMA_ELEMENT_NAME, experimentId));
	}
	
	private final AssessorService _assessorService;
}
