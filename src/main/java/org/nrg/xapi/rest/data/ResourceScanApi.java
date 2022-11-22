package org.nrg.xapi.rest.data;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.nrg.xnat.services.archive.ResourceScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Api("XNAT Resource Scanning API")
@XapiRestController
@RequestMapping(value = "/resources")
@Slf4j
public class ResourceScanApi extends AbstractXapiRestController {
    private final ResourceScanService _resourceScanService;

    @Autowired
    public ResourceScanApi(final ResourceScanService resourceScanService, final UserManagementServiceI userManagementService, final RoleHolder roleHolder) {
        super(userManagementService, roleHolder);
        _resourceScanService = resourceScanService;
    }

    @ApiOperation(value = "Get the resource scan requests for resources in the specified project", notes = "Returns the resource scan requests for the specified project", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the resource scan requests for the specified project ID."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to access resource scan requests."),
                   @ApiResponse(code = 404, message = "No project exists with the specified ID."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/project/{projectId}", produces = APPLICATION_JSON_VALUE, restrictTo = AccessLevel.Delete)
    public List<ResourceScanRequest> getReport(final @PathVariable String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.getByProject(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Create resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 404, message = "No project exists with the specified ID."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/project/{projectId}", produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public List<ResourceScanRequest> createResourceScanRequestsForProject(final @PathVariable String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.createResourceScanRequests(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Get the resource scan request for the specified resource", notes = "Returns the requested scan request", response = ResourceScanRequest.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the resource scan request for the specified resource ID."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to access resource scan requests."),
                   @ApiResponse(code = 404, message = "No resource scan request exists for the specified resource ID."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, restrictTo = AccessLevel.Delete)
    public ResourceScanRequest getReport(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.getByResourceId(getSessionUser(), resourceId);
    }

    @ApiOperation(value = "Create resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public ResourceScanRequest createResourceScanRequest(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.createResourceScanRequest(getSessionUser(), resourceId);
    }

    @ApiOperation(value = "Create resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = AccessLevel.Delete)
    public ResourceScanReport scanResource(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.scanResource(getSessionUser(), resourceId);
    }

    @ApiOperation(value = "Repair resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "repair/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public ResourceMitigationReport repairResource(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException, InitializationException {
        return _resourceScanService.repairResource(getSessionUser(), resourceId);
    }
}
