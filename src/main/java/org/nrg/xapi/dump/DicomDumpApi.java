package org.nrg.xapi.dump;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.services.dump.DicomDumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT config Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class DicomDumpApi extends AbstractXapiProjectRestController {
	

	@Autowired
	public DicomDumpApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final DicomDumpService dicomDumpService) {
		 super(userManagementService, roleHolder);
		 _dicomDumpService = dicomDumpService;
	}
	
	@ApiOperation(value = "Gets the requested  config", notes = "Returns the  project with the specified ID", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested config wasn't found."),
			@ApiResponse(code = 404, message = "The requested config wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/services/dicomdump", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<DicomSummary> getAllConfigs(@ApiParam(value = "The value to src.")@RequestParam(name = "src") String src, 
											@ApiParam(value = "The value to summary.")@RequestParam(name = "summary", required = false) String summary,
											@ApiParam(value = "The value to fields.")@RequestParam(name = "fields", required = false) String[] fields)
			throws Exception {
		log.debug("User {} requested configs", getSessionUser().getUsername());
		return _dicomDumpService.findAll(getSessionUser(), src, summary, fields);
	}
	private final DicomDumpService _dicomDumpService;
}
