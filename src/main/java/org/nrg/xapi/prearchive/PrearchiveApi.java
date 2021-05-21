package org.nrg.xapi.prearchive;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.SQLException;
import java.util.List;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.helpers.prearchive.SessionException;
import org.nrg.xnat.services.prearchive.PrearchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Prearchiv Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class PrearchiveApi extends AbstractXapiProjectRestController {
	
    @Autowired
    public PrearchiveApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final PrearchiveService prearchiveService) {
        super(userManagementService, roleHolder);
        _prearchiveService = prearchiveService;
    }
    
    @ApiOperation(value = "Gets the requested  Prearchive", notes = "Returns the  Prearchive with the specified PROJECT ID", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested project."),
			@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
			@ApiResponse(code = 404, message = "The requested Prearchive wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = {"/prearchive","/prearchive/projects/{projectId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<PrearchiveDto> getAllProjectConfigs(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
													@ApiParam(value = "The value of the tag.") @RequestParam(name = "tag", required = false) final String tag) throws SQLException, SessionException, Exception {
		log.debug("User {} requested configs", getSessionUser().getUsername());
		return _prearchiveService.findAllPrearchives(getSessionUser(), projectId, tag);
	}
   private final PrearchiveService _prearchiveService;
}
