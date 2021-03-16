package org.nrg.xapi.scans;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatScscandata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.scans.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@ApiOperation(value = "Get scan of  specified scanId", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatImagescandata>> getExperimentScans(@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId) throws Exception {
		log.debug("Controller Api- get scans");
		List<XnatImagescandata> xnatImagescandatas = _scanService.findByAssessed(getSessionUser(), assessedId);
		if (xnatImagescandatas == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatImagescandatas, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatImagescandata> getExperimentScansByassessedAndId(@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId,
			@ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final Integer scanId) throws Exception {
		log.debug("Controller Api- get scans");
		XnatImagescandata xnatImagescandata = _scanService.findByAssessedAndScan(getSessionUser(), assessedId, scanId);
		if (xnatImagescandata == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatImagescandata, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatImagescandata>> getScansByProjectAndSubjectAndExperiment(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId) throws Exception {
		log.debug("Controller Api- get scans");
		List<XnatImagescandata> xnatImagescandatas = _scanService.findByProjectAndSubjectAndExperiment(getSessionUser(),projectId,subjectId , experimentId);
		if (xnatImagescandatas == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatImagescandatas, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get scan of specified scanId", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatImagescandata> getScansByProjectAndSubjectAndExperiment(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final Integer scanId) throws Exception {
		log.debug("Controller Api- get scans");
		XnatImagescandata xnatImagescandata = _scanService.findByProjectAndSubjectAndExperimentAndScan(getSessionUser(),projectId,subjectId , experimentId, scanId);
		if (xnatImagescandata == null) {
			throw new NotFoundException("No Subject with data was found.");
		}
		return new ResponseEntity<>(xnatImagescandata, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Delete an existing scan", notes = "Deletes the specified scan.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified scan."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified scan"),
                   @ApiResponse(code = 404, message = "The specified scan or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}",
    		"/experiments/{assessedId}/scans/{scanId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteProject(@ApiParam("The ID of the project to be deleted") @PathVariable(required = false) final String projectId,
    		@ApiParam("The ID of the subject to be deleted") @PathVariable(required = false) final String subjectId,
    		@ApiParam("The ID of the experiment to be deleted") @PathVariable final String assessedId,
    		@ApiParam("The ID of the scan to be deleted") @PathVariable final Integer scanId) throws Exception {
        log.debug("Controller Api- Delete scan {}", assessedId);
        _scanService.deleteById(getSessionUser(), assessedId, scanId);
    }
	
	 @ApiOperation(value = "Create a new scan", notes = "Creates the submitted scan.", response = XnatImagescandata.class)
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created scan."),
	    @ApiResponse(code = 403, message = "The user doesn't have permission to create scan in the specified project"),
	    @ApiResponse(code = 404, message = "The specified project doesn't exist"),
	    @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans", "/experiments/{assessedId}/scans"},
	                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        method = POST)
	    public XnatImagescandata createScan(@ApiParam("The project in which the experiment should be created") @PathVariable(required = false) final String projectId,
	    		@ApiParam("The subject in which the scan should be created") @PathVariable(required = false) final String subjectId,
	    		@ApiParam("The ID of the experiment to be created") @PathVariable final String assessedId,
	            @ApiParam("The scan to be created.") @RequestBody final XnatImagescandata scan, @RequestParam(required = false) String label) throws Exception {
	        log.debug("Controller Api- Create scan: {}", scan);
	        final boolean scanHasProject = StringUtils.isNotBlank(scan.getProject());
	        final boolean hasProject        = StringUtils.isNotBlank(projectId);
	        if (!scanHasProject && !hasProject) {
	            throw new DataFormatException("You must specify a project in which the scan should be created.");
	        }
	        if (scanHasProject && hasProject && !StringUtils.equals(scan.getProject(), projectId)) {
	            throw new DataFormatException("You specified the project " + projectId + " in your request but the scan is assigned to project " + scan.getProject() + ". These values must be the same.");
	        }
	        if (!scanHasProject) {
	        	scan.setProject(projectId);
	        }
	         return _scanService.create(getSessionUser(), projectId, subjectId, assessedId, scan);
	    }

	
	private final ScanService _scanService;
}
