package org.nrg.xapi.scans;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xapi.subjects.SubjectApi;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatScscandata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.scans.ScanService;
import org.nrg.xnat.services.subjects.SubjectService;
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

@Api("XNAT Scan Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ScanApi extends AbstractXapiProjectRestController {

	@Autowired
	public ScanApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ScanService scanService) {
		super(userManagementService, roleHolder);
		_scanService = scanService;
	}
	
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatImagescandata>> getProjectScanTypes(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
		log.debug("Controller Api- get all project scans");
		List<XnatImagescandata> xnatImagescandatas = _scanService.findScanTypesByProject(getSessionUser(), projectId);
		if (xnatImagescandatas == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatImagescandatas, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatImagescandata>> getAllScanTypes() throws Exception {
		log.debug("Controller Api- get scan types");
		List<XnatImagescandata> xnatImagescandatas = _scanService.getAllScanTypes(getSessionUser());
		if (xnatImagescandatas == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatImagescandatas, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatImagescandata>> getExperimentScans(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId) throws Exception {
		log.debug("Controller Api- get scans");
		List<XnatImagescandata> xnatImagescandatas = _scanService.findByExperiments(getSessionUser(), experimentId);
		if (xnatImagescandatas == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatImagescandatas, HttpStatus.OK);
	}

	
	private final ScanService _scanService;
}
