package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.subjects.XnatProject;
import org.nrg.xapi.model.subjects.XnatScan;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ProjectListService;
import org.nrg.xnat.services.resources.ScanListService;
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

@Api("XNAT Scan list  Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ScanListApi extends AbstractXapiProjectRestController {
	 
	@Autowired
	public ScanListApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,final ScanListService scanListService) {
		super(userManagementService, roleHolder);
		_scanListService = scanListService;
	}
	
	@ApiOperation(value = "Get list of scans", notes= "The projects function returns a list of all scans configured in the XNAT system.", response = XnatScan.class, responseContainer = "single")
	@ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured projects."),
       @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans" , produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatScan>> getScanList(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String assessedId) throws Exception {
		log.debug("Controller Api- get Resources by projectId");
		List<XnatScan> xnatScans = _scanListService.findScansByExperimentId(getSessionUser(), assessedId);
	    if (xnatScans == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(xnatScans, HttpStatus.OK);
	}
	
	private final ScanListService _scanListService;
}
