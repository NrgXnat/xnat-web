package org.nrg.xapi.prearchive;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.services.prearchive.PrearchiveRebuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;


@Api("XNAT prearchive rebuild Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class PrearchiveRebuildApi extends AbstractXapiProjectRestController {
    @Autowired
    public PrearchiveRebuildApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final PrearchiveRebuildService prearchiveRebuildService) {
        super(userManagementService, roleHolder);
        _prearchiveRebuildService = prearchiveRebuildService;
    }
	
	@ApiOperation(value = "Create a new prearchive rebuild", notes = "Creates the submitted v.", response = XnatProjectdata.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the newly created project."),
			@ApiResponse(code = 400, message = "The requested prearchive rebuild wasn't found."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to create prearchive rebuild"),
			@ApiResponse(code = 404, message = "The specified prearchive rebuild doesn't exist"),
			@ApiResponse(code = 409, message = "The specified prearchive rebuild already exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = "/services/prearchive/rebuild ",   consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
						produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, method = POST)
	public PrearchiveDto createPrarchiveRebuild(@ApiParam("The file to be rebuild session.")  @RequestParam MultipartFile file,
												@ApiParam("The value to src") @RequestParam(name = "src") String src){
		log.debug("User {} requested to create prearchive rebuild  with src {}", getSessionUser().getUsername(), src);
		return _prearchiveRebuildService.createPrarchiveRebuild(getSessionUser(), src, file);
	}

	private final PrearchiveRebuildService _prearchiveRebuildService;
}
