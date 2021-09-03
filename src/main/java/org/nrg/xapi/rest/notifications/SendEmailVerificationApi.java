package org.nrg.xapi.rest.notifications;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.extensions.SendEmailVerificationService;
import org.nrg.xnat.services.extensions.impl.SendEmailVerificationServiceImpl.EmailNotFoundException;
import org.nrg.xnat.services.extensions.impl.SendEmailVerificationServiceImpl.ExceededRequestsException;
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

@Api("XNAT Send Email Verification Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class SendEmailVerificationApi extends AbstractXapiProjectRestController {

	@Autowired
    public SendEmailVerificationApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final SendEmailVerificationService sendEmailVerificationService) {
        super(userManagementService, roleHolder);
        _sendEmailVerificationService = sendEmailVerificationService;
    }
	
	@ApiOperation(value = "Send Email Verification", notes = " Send Email Verification", response = void.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Send Email Verification."),
    	           @ApiResponse(code = 400, message = "The requested email wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Send Email Verification wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	 @XapiRequestMapping(value = "/services/sendEmailVerification", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
     																produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
    public void sendEmail(@ApiParam(value = "The value of email.") @RequestParam final String email) throws InitializationException, ExceededRequestsException, EmailNotFoundException {
		log.debug("User {} requested Send Email Verification", getSessionUser().getUsername());
		 _sendEmailVerificationService.sendEmail(getSessionUser(), email);
	}
	
	private final SendEmailVerificationService _sendEmailVerificationService;

}
