package org.nrg.xapi.rest.data;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.ConflictedStateException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.nrg.xnat.services.archive.ResourceScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

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

    @ApiOperation(value = "Create resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of new resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 404, message = "No project exists with the specified ID."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/project/{projectId}", produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public List<ResourceScanRequest> createResourceScanRequestsForProject(final @PathVariable String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.createResourceScanRequests(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Create a resource scan request for the specified resource", notes = "Returns the newly generated request", response = ResourceScanRequest.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the new resource scan request for the specified resource."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public ResourceScanRequest createResourceScanRequest(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.createResourceScanRequest(getSessionUser(), resourceId);
    }

    @ApiOperation(value = "Gets resource scan requests with status \"Created\" for resources in the specified project", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns resource scan requests for the specified project ID."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to access resource scan requests."),
                   @ApiResponse(code = 404, message = "No project exists with the specified ID."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/project/{projectId}", produces = APPLICATION_JSON_VALUE, restrictTo = AccessLevel.Delete)
    public List<ResourceScanRequest> getResourceScanRequestsByProject(final @PathVariable String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return getResourceScanRequestsByProjectAndStatus(projectId, null);
    }

    @ApiOperation(value = "Get resource scan requests for resources in the specified project", notes = "By default, this returns only queued resource scan requests for the specified project, but you can specify another status or \"all\" to return all requests for the project.", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns resource scan requests for the specified project ID."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to access resource scan requests."),
                   @ApiResponse(code = 404, message = "No project exists with the specified ID."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/project/{projectId}/{status}", produces = APPLICATION_JSON_VALUE, restrictTo = AccessLevel.Delete)
    public List<ResourceScanRequest> getResourceScanRequestsByProjectAndStatus(final @PathVariable String projectId, final @PathVariable String status) throws InsufficientPrivilegesException, NotFoundException {
        if (StringUtils.isBlank(status)) {
            return _resourceScanService.getByProject(getSessionUser(), projectId, ResourceScanRequest.Status.Created);
        }
        return StringUtils.equals("all", status)
               ? _resourceScanService.getByProject(getSessionUser(), projectId)
               : _resourceScanService.getByProject(getSessionUser(), projectId, ResourceScanRequest.Status.valueOf(status));
    }

    @ApiOperation(value = "Get the resource scan request for the specified resource", notes = "Returns the requested scan request", response = ResourceScanRequest.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the resource scan request for the specified resource ID."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to access resource scan requests."),
                   @ApiResponse(code = 404, message = "No resource scan request exists for the specified resource ID."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, restrictTo = AccessLevel.Delete)
    public ResourceScanRequest getResourceScanRequest(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.getByResourceId(getSessionUser(), resourceId);
    }

    @ApiOperation(value = "Create scan reports from resource scan requests with status \"Created\" for the specified project", notes = "Returns the newly generated scan reports", response = ResourceScanReport.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan reports for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 409, message = "There is no resource scan request for the specified resource or the request status is not queued."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/project/{projectId}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = AccessLevel.Delete)
    public List<ResourceScanReport> scanResource(final @PathVariable String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.scanResources(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Create resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 409, message = "There is no resource scan request for the specified resource or the request status is not queued."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "scan/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, method = PUT, restrictTo = AccessLevel.Delete)
    public ResourceScanReport scanResource(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException, ConflictedStateException {
        return _resourceScanService.scanResource(getSessionUser(), resourceId);
    }

    @ApiOperation(value = "Queues repair resource scan requests for the specified project", notes = "Returns a list of workflow IDs for the queued repair requests", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of workflow IDs for resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to queue resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "repair/project/{projectId}", produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public List<Integer> repairProjectResources(final @PathVariable String projectId) throws InsufficientPrivilegesException, NotFoundException {
        return _resourceScanService.queueRepairResourcesForProject(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Repair resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "repair/resource/{resourceId}", produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public Integer repairResource(final @PathVariable int resourceId) throws InsufficientPrivilegesException, NotFoundException, InitializationException {
        return _resourceScanService.queueRepairResource(getSessionUser(), resourceId);
    }

    @ApiOperation(value = "Repair resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "repair/status/{workflowId}", produces = APPLICATION_JSON_VALUE, restrictTo = AccessLevel.Delete)
    public String getRepairStatus(final @PathVariable int workflowId) throws NotFoundException, InsufficientPrivilegesException {
        return _resourceScanService.getRepairStatus(getSessionUser(), workflowId);
    }

    @ApiOperation(value = "Repair resource scan requests for the specified project", notes = "Returns the newly generated requests", response = ResourceScanRequest.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of resource scan requests for the specified project."),
                   @ApiResponse(code = 403, message = "Insufficient permissions to generate resource scan requests."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "repair/status", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE, method = POST, restrictTo = AccessLevel.Delete)
    public Map<Integer, String> getRepairStatuses(final List<Integer> workflowIds) {
        return _resourceScanService.getRepairStatuses(getSessionUser(), workflowIds);
    }
}
