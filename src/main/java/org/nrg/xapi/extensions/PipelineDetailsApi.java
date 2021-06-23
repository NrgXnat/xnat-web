package org.nrg.xapi.extensions;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.extensions.PipelineDetailsService;
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

@Api("XNAT Pipeline Details Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class PipelineDetailsApi extends AbstractXapiProjectRestController {
	
	@Autowired
    public PipelineDetailsApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final PipelineDetailsService pipelineDetailsService) {
        super(userManagementService, roleHolder);
        _pipelineDetailsService = pipelineDetailsService;
    }
	
	@ApiOperation(value = "Gets the Pipeline Details", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Pipeline Details wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/projects/{projectId}/pipelines/{pipelineName}","/projects/{projectId}/pipelines/{pipelineName}/details"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public String getAllIpWhiteList(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
    		@ApiParam(value = "The ID of the project.") @PathVariable final String pipelineName) throws DataFormatException, InitializationException, NotFoundException {
		log.debug("User {} requested IpWhitelist", getSessionUser().getUsername());
		return _pipelineDetailsService.findAllPiperlineDetails(getSessionUser(), projectId, pipelineName).orElseThrow(() -> new NotFoundException("The pipeline details was't found with specified project Id " + projectId + "And with specified Pipeline Name "+ pipelineName));
	}
	
	private final PipelineDetailsService _pipelineDetailsService;

}
