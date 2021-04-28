package org.nrg.xapi.pars;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.par.PARService;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/pars", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ProjectAccessRequest> getAll() throws NotFoundException {
    	log.debug("User {} requested ProjectAccessRequest ", getSessionUser().getUsername());
    	return _parService.findAll(getSessionUser()).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME));
    }
    
    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
    			   @ApiResponse(code = 400, message = "The requested projectId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/pars", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ProjectAccessRequest> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
    	log.debug("User {} requested project with ID {} ", getSessionUser().getUsername(), projectId);
    	return _parService.findByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME));
    }
    
    @ApiOperation(value = "Gets the requested  project access request", notes = "Returns the  project with the specified ID", response = ProjectAccessRequest.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested ProjectAccessRequest."),
    			   @ApiResponse(code = 400, message = "The requested parId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested ProjectAccessRequest wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/pars/{parId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ProjectAccessRequest getByParId(@ApiParam(value = "The ID of the project access request.") @PathVariable final Integer parId) throws NotFoundException, DataFormatException {
    	log.debug("User {} requested parId with ID {} ", getSessionUser().getUsername(), parId);
    	return _parService.findByParId(getSessionUser(), parId).orElseThrow(() -> new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, parId));
    }
    
    @ApiOperation(value = "Update an existing projectAccessRequest", notes = "Updates the submitted projectAccessRequest.", response = ProjectAccessRequest.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated projectAccessRequest."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit projectAccessRequest in the specified projectAccessRequest"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/pars/{parId}",
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = PUT)
    public ProjectAccessRequest updateProjectAccessRequest(@ApiParam("The ID of the project to be updated") @PathVariable final Integer parId,
    													   @ApiParam("The ID of the project to be updated") @RequestParam final  String accept,
    													   @ApiParam("The ID of the project to be updated") @RequestParam final  String decline,
    													   @ApiParam("The project to be updated.") @RequestBody final ProjectAccessRequest projectAccessRequest,
    													   @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    													   @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    													   @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    													   @ApiParam("The event  action value ") @RequestParam(name = "eventAction", defaultValue = "Deleted")String eventAction,
    													   @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws NotFoundException  {
    	log.debug("User {} requested to update projectAccessRequest with ID {}", getSessionUser().getUsername(), parId);
        return _parService.update(getSessionUser(), projectAccessRequest, parId, accept, decline, new XnatEventUtil().getXnatEventUtil(eventType, eventReason, eventId, eventAction, eventComment));
    }
    
    private final PARService _parService;
}
