package org.nrg.xapi.archive;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.archive.FileResourceService;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api("XNAT File Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class FileResourceApi extends AbstractXapiProjectRestController {
    public FileResourceApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final FileResourceService fileResourceService) {
        super(userManagementService, roleHolder);
        _fileResourceService = fileResourceService;
    }

    @ApiOperation(value = "Get the resources for a project, subject, or experiment",
                  notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources or /projects/XNAT_01/subjects/XNAT_01_01/resources.",
                  response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
                   @ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}/resources",
                                 //"/experiments/{experimentId}/resources",
                                 "/experiments/{assessedId}/assessors/{experimentId}/resources",
                                 "/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/resources",
                                 //"/experiments/{assessedId}/scans/{scanId}/resources",
                                 "/subjects/{subjectId}/resources"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatAbstractresourceI> getResources(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                                    @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
                                                    @ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
                                                    @ApiParam(value = "The ID of the assessed experiment.") @PathVariable(required = false) final String assessedId,
                                                    @ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId,
                                                    @ApiParam(value = "The resource type (must be in or out).", defaultValue = "out") @PathVariable(required = false) final String resourceType) throws NotFoundException {
        return _fileResourceService.getResources(getSessionUser(), getItemId(projectId, subjectId, experimentId, assessedId, scanId, resourceType));
    }

    @ApiOperation(value = "Get a specified resource for a project, subject, or experiment", notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources/FOO or /projects/XNAT_01/subjects/XNAT_01_01/resources/13.")
    @ApiResponses({@ApiResponse(code = 200, message = "The resource was successfully retrieved."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item"),
                   @ApiResponse(code = 403, message = "The the requested item doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/experiments/{assessedId}/assessors/{experimentId}/resources/{resourceId}",
                                 "/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/resources/{resourceId}",
                                 //"/experiments/{assessedId}/scans/{scanId}/resources/{resourceId}",
                                // "/experiments/{experimentId}/resources/{resourceId}",
                                 "/projects/{projectId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}",
                                 "/subjects/{subjectId}/resources/{resourceId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public XnatAbstractresourceI getResource(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                             @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
                                             @ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
                                             @ApiParam(value = "The ID of the assessed experiment.") @PathVariable(required = false) final String assessedId,
                                             @ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId,
                                             @ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final String resourceId,
                                             @ApiParam(value = "The resource type (must be in or out).", defaultValue = "out") @PathVariable(required = false) final String resourceType) throws NotFoundException {
        return _fileResourceService.getResource(getSessionUser(), getResourceId(projectId, subjectId, experimentId, assessedId, scanId, resourceId, resourceType));
    }

    @ApiOperation(value = "Get the files for a resource associated with a project, subject, or experiment",
                  notes = "The resource should be identified by standard item-relative paths, such as /experiments/XNAT_E0001/resources/1/files or /projects/XNAT_01/subjects/XNAT_01_01/resources/ITEMS/files.",
                  response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "The resource file(s) were successfully retrieved."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
                   @ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}/resources/{resourceId}/files",
                                // "/experiments/{experimentId}/resources/{resourceId}/files",
                                 "/experiments/{assessedId}/assessors/{experimentId}/resources/{resourceId}/files",
                                 "/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/resources/{resourceId}/files",
                                 //"/experiments/{assessedId}/scans/{scanId}/resources/{resourceId}/files",
                                 "/subjects/{subjectId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}/files",
                                 //"/experiments/{experimentId}/files",
                                 "/experiments/{assessedId}/assessors/{experimentId}/{resourceType}/files",
                                 "/experiments/{assessedId}/assessors/{experimentId}/files",
                                 //"/experiments/{assessedId}/scans/{scanId}/files",
                                 "/subjects/{subjectId}/files"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<Resource> getResourceFiles(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                         @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
                                         @ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
                                         @ApiParam(value = "The ID of the assessed experiment.") @PathVariable(required = false) final String assessedId,
                                         @ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId,
                                         @ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final String resourceId,
                                         @ApiParam(value = "The resource type (must be in or out).", defaultValue = "out") @PathVariable(required = false) final String resourceType) throws NotFoundException {
        return _fileResourceService.getResourceFiles(getSessionUser(), getResourceId(projectId, subjectId, experimentId, assessedId, scanId, resourceId, resourceType));
    }

    private final FileResourceService _fileResourceService;
}
