package org.nrg.xapi.rest.automation;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.runner.RunnerService;
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

@Api("XNAT Runner Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class RunnerApi extends AbstractXapiProjectRestController {

	@Autowired
	public RunnerApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final RunnerService runnerService) {
		super(userManagementService, roleHolder);
		_runnerService = runnerService;
	}
	
	@ApiOperation(value = "Get list of runners", notes = "The runners function returns a list of all runners configured in the XNAT system.", response = String.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured runners."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = {"/automation/runners", "/automation/runners/{language}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public String getAutomationRunners(@ApiParam(value = "The language string.") @PathVariable(required = false) final String language) throws Exception {
		log.debug("Fetch the all automation runners ");
		String runners = _runnerService.getAutomationRunners(language).orElseThrow(() -> new NotFoundException("Automation runners wasn't found"));
		return runners.replace("\\", "");
	}
	
	private final RunnerService _runnerService;

}
