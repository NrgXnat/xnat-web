package org.nrg.xapi.extensions;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.extensions.VerifyExtensionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT  Verify Extensions Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class VerifyExtensionsApi extends AbstractXapiProjectRestController {
	

	@Autowired
    public VerifyExtensionsApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final VerifyExtensionsService verifyExtensionsService) {
        super(userManagementService, roleHolder);
        _verifyExtensionsService = verifyExtensionsService;
    }
	
	@ApiOperation(value = "Gets the Verify Extensions", notes = "Returns the  Verify Extensions", response = String.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested Verify Extensions wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Verify Extensions wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "//services/extensions/verify", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public String getVerifyExtension() {
		log.debug("User {} requested Verify Extensions", getSessionUser().getUsername());
		return _verifyExtensionsService.findVerifyExtension(getSessionUser());
	}
	
	private final VerifyExtensionsService _verifyExtensionsService;
}
