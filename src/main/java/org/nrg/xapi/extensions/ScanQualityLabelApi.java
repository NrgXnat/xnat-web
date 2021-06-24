package org.nrg.xapi.extensions;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.json.JSONObject;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.extensions.ScanQualityLabelService;
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

@Api("XNAT Scan Quality Label Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ScanQualityLabelApi extends AbstractXapiProjectRestController {
	

	@Autowired
    public ScanQualityLabelApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ScanQualityLabelService scanQualityLabelService) {
        super(userManagementService, roleHolder);
        _scanQualityLabelService = scanQualityLabelService;
    }
	
	@ApiOperation(value = "Gets the Scan Quality Lable", notes = "Returns the  Scan Quality Lable", response = String.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Scan Quality Lable wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/services/scan-quality-labels", "/services/scan-quality-labels/{projectId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public String getAllScanQualityLable(@ApiParam("The ID of the project to be updated") @PathVariable(required = false) final String projectId) throws InitializationException  {
		log.debug("User {} requested Scan Quality Lable", getSessionUser().getUsername());
		return _scanQualityLabelService.findAllScanQualityLable(getSessionUser(), projectId);
	}
	
	
	private final ScanQualityLabelService _scanQualityLabelService;

}
