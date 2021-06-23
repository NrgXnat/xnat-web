package org.nrg.xapi.extensions;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.IOException;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.extensions.IpWhitelistService;
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

@Api("XNAT IpWhitelist Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class IpWhitelistApi extends AbstractXapiProjectRestController {
	
	@Autowired
    public IpWhitelistApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final IpWhitelistService ipWhitelistService) {
        super(userManagementService, roleHolder);
        _ipWhitelistService = ipWhitelistService;
    }

	
	@ApiOperation(value = "Gets the Ip Whitelist", notes = "Returns the  IpWhitelist", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested IpWhitelist wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/services/ipwhitelist", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public String getAllIpWhiteList() throws NotFoundException, DataFormatException, InsufficientPrivilegesException, InitializationException {
		log.debug("User {} requested IpWhitelist", getSessionUser().getUsername());
		return _ipWhitelistService.findAllIpWhiteList(getSessionUser());
	}


	@ApiOperation(value = "Update an existing IpWhitelist", notes = "Updates the submitted project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit IpWhitelist"),
                   @ApiResponse(code = 404, message = "The specified IpWhitelist doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/services/ipwhitelist",  consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        						produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = PUT)
	public void updateProject(@ApiParam("The ID of the project to be updated") @RequestParam final String whiteList) throws InitializationException, IOException {
		log.debug("User {} requested to update IpWhitelist with IP {}", getSessionUser().getUsername(), whiteList);
		_ipWhitelistService.updateIpWhiteList(getSessionUser(), whiteList);
	}

	 private final IpWhitelistService _ipWhitelistService;
}
