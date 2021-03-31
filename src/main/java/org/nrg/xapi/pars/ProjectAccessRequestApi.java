package org.nrg.xapi.pars;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.par.PARService;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;
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

@Api("XNAT PARS Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectAccessRequestApi extends AbstractXapiProjectRestController {
    @Autowired
    public ProjectAccessRequestApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final PARService parService) {
        super(userManagementService, roleHolder);
        _parService = parService;
    }
    
    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/pars", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<List<ProjectAccessRequest>> getProjectAccessRequestByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId) throws Exception {
        log.debug("Controller Api- get ProjectAccessRequest by projectId {}", projectId);
        List<ProjectAccessRequest> projectAccessRequests = _parService.getProjectParsByProjectId(getSessionUser(), projectId);
        if (projectAccessRequests == null) {
            throw new NotFoundException("No ProjectAccessRequest with projectId {}" + projectId + " was found.");
        }
        return new ResponseEntity<>(projectAccessRequests, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/pars/{parId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<ProjectAccessRequest> getProjectAccessRequestByParId(@ApiParam(value = "The ID of the project access request.") @PathVariable(required = false) final Integer parId) throws Exception {
        log.debug("Controller Api- get project access request by parId {}", parId);
        ProjectAccessRequest projectAccessRequests = _parService.getParResourceByParId(getSessionUser(), parId);
        if (projectAccessRequests == null) {
            throw new NotFoundException("No pars with parId {} " + parId + " was found.") ;
        }
        return new ResponseEntity<>(projectAccessRequests, HttpStatus.OK);
    }
    
    private final PARService _parService;
}
