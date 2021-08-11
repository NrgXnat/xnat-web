package org.nrg.xapi.workflow;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.workflow.WorkflowService;
import org.nrg.xnat.services.workflow.dto.WrkWorkflowdataDto;
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

@Api("XNAT WrkWorkflowdata Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class WorkflowApi extends AbstractXapiProjectRestController {
	
    @Autowired
    public WorkflowApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final WorkflowService workflowService) {
        super(userManagementService, roleHolder);
        _workflowService = workflowService;
    }
    
    @ApiOperation(value = "Gets the requested  WrkWorkflowdata", notes = "Returns the  WrkWorkflowdatas", response = WrkWorkflowdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested WrkWorkflowdatas."),
    	           @ApiResponse(code = 400, message = "The requested WrkWorkflowdata wasn't found."),
                   @ApiResponse(code = 404, message = "The requested WrkWorkflowdata wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/workflows", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<WrkWorkflowdataDto> getAllWrkWorkflowdatas() throws NotFoundException {
    	log.debug("User {} requested WrkWorkflowdata", getSessionUser().getUsername());
        return _workflowService.findAllWrkWorkflowdata(getSessionUser());
    }
    
    @ApiOperation(value = "Gets the requested  WrkWorkflowdata", notes = "Returns the  WrkWorkflowdata with the specified  ID", response = WrkWorkflowdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested WrkWorkflowdatas."),
    	           @ApiResponse(code = 400, message = "The requested workflow Id wasn't found."),
                   @ApiResponse(code = 404, message = "The requested WrkWorkflowdata wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/workflows/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public WrkWorkflowdata getWrkWorkflowdataById(@ApiParam(value = "The ID of the workflow Id.") @PathVariable final String workflowId) throws NotFoundException, DataFormatException {
    	log.debug("User {} requested WrkWorkflowdata", getSessionUser().getUsername());
        return _workflowService.findWrkWorkflowdata(getSessionUser(), workflowId);
    }
    
    private final WorkflowService _workflowService;
}
