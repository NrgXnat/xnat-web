package org.nrg.xapi.scans;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatScscandata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.scans.ScanService;
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
	
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatImagescandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
					@ApiResponse(code = 400, message = "The requested projectId  wasn't found."),
					@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public  List<XnatImagescandata> getScanTypesByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
		return _scanService.findAllScanTypesByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, projectId));
	}
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatImagescandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
					@ApiResponse(code = 404, message = "The requested scan types wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/scan_types", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatImagescandata> getAllScanTypes() throws NotFoundException {
		log.debug("User {} requested scan types", getSessionUser().getUsername());
		return _scanService.findAllScanTypes(getSessionUser()).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME));
	}
	
	@ApiOperation(value = "Get scan of  specified scanId", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
					@ApiResponse(code = 400, message = "The requested assessedId  wasn't found."),
					@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatImagescandata> getAllByAssessedId(@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested assessor with ID {}", getSessionUser().getUsername(), assessedId);
		return _scanService.findAllByAssessedId(getSessionUser(), assessedId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, assessedId));
	}
	
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
					@ApiResponse(code = 400, message = "The requested either assessedId or scanId wasn't found."),
					@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XnatImagescandata getByAssessedIdAndScanId(@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
			@ApiParam(value = "The ID of the scan.") @PathVariable  final Integer scanId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested assessor with ID {} and scan with ID {}", getSessionUser().getUsername(), assessedId, scanId);
		return _scanService.findByAssessedIdAndScanId(getSessionUser(), assessedId, scanId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, assessedId));
	}
	
	@ApiOperation(value = "Get list of scans", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId wasn't found."),
					@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatImagescandata> getScansByProjectAndSubjectAndExperiment(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException{
		log.debug("User {} requested project with ID {}, subject with ID {} and experiment with ID {}", getSessionUser().getUsername(), projectId, subjectId, experimentId);
		return _scanService.findAllByProjectIdAndSubjectIdAndExperimentId(getSessionUser(),projectId,subjectId , experimentId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, experimentId));
	}
	
	@ApiOperation(value = "Get scan of specified scanId", notes = "The scans function returns a list of all scans configured in the XNAT system.", response = XnatScscandata.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns a list of all of the currently configured scans."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or scanId wasn't found."),
					@ApiResponse(code = 404, message = "The requested scans wasn't found."),			
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/scans/{scanId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public XnatImagescandata getByProjectIdAndSubjectIdAndExperimentIdAndScanId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
			@ApiParam(value = "The ID of the scan.") @PathVariable final Integer scanId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested project with ID {}, subject with ID {}, experiment with ID {} and scan with ID {}", getSessionUser().getUsername(), projectId, subjectId, experimentId, scanId);
		return _scanService.findByProjectIdAndSubjectIdAndExperimentIdAndScanId(getSessionUser(),projectId,subjectId , experimentId, scanId).orElseThrow(() -> new NotFoundException(XnatImagescandata.SCHEMA_ELEMENT_NAME, experimentId));
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
	
	private final ScanService _scanService;
}
