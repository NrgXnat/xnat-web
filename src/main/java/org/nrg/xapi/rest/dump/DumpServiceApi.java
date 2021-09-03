package org.nrg.xapi.rest.dump;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.helpers.ecat.EcatSummary;
import org.nrg.xnat.services.dump.DumpService;
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

@Api("XNAT dicom dump Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class DumpServiceApi extends AbstractXapiProjectRestController {
	

	@Autowired
	public DumpServiceApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final DumpService dumpService) {
		 super(userManagementService, roleHolder);
		 _dumpService = dumpService;
	}
	
	@ApiOperation(value = "Gets the requested  dicom summary", notes = "Returns the  dicom summary", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested dicom summary wasn't found."),
			@ApiResponse(code = 404, message = "The requested dicom summary wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/services/dicomdump", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<DicomSummary> getAllDicomSummary(@ApiParam(value = "The value to src.")@RequestParam(name = "src") String src, 
											@ApiParam(value = "The value to summary.")@RequestParam(name = "summary", required = false) String summary,
											@ApiParam(value = "The value to field.")@RequestParam(name = "field", required = false) String[] field)
			throws Exception {
		log.debug("User {} requested dicom summary", getSessionUser().getUsername());
		return _dumpService.findAllDicomSummary(getSessionUser(), src, summary, field);
	}
	
	

	@ApiOperation(value = "Gets the requested  dicom summary", notes = "Returns the  dicom summary", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested dicom summary wasn't found."),
			@ApiResponse(code = 404, message = "The requested dicom summary wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/services/ecatdump", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<EcatSummary> getAllEcatSummary(@ApiParam(value = "The value to src.")@RequestParam(name = "src") String src, 
											@ApiParam(value = "The value to summary.")@RequestParam(name = "summary", required = false) String summary,
											@ApiParam(value = "The value to field.")@RequestParam(name = "field", required = false) String[] field)
			throws Exception {
		log.debug("User {} requested dicom summary", getSessionUser().getUsername());
		return _dumpService.findAllEcatSummary(getSessionUser(), src, summary, field);
	}
	private final DumpService _dumpService;
}
