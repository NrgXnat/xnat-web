package org.nrg.xapi.rest.data;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.generics.GenericUtils;
import org.nrg.xapi.authorization.CreateProjectXapiAuthorization;
import org.nrg.xapi.exceptions.*;
import org.nrg.xapi.model.xft.DicomDir;
import org.nrg.xapi.model.ResourceFile;
import org.nrg.xapi.model.xft.TriageEntry;
import org.nrg.xapi.rest.*;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatResourceI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.resource.MediaTypeUtil;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.resources.ResourceService;
import org.nrg.xnat.services.resources.impl.ResourceServiceImpl.InvalidFileCharacters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api("XNAT Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ResourceApi extends AbstractXapiProjectRestController {
    @Autowired
    public ResourceApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ResourceService resourceService) {
        super(userManagementService, roleHolder);
        _resourceService = resourceService;
    }

    @ApiOperation(value = "Gets the requested resources", notes = "Returns the  resources with the specified Experiment ID", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resources."),
                   @ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resources wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatAbstractresourceI> getByExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources with experiment ID {} ", getSessionUser().getUsername(), experimentId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByExperimentId(getSessionUser(), experimentId), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresourceI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either experimentId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatAbstractresourceI getByIdAndExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                        @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources with experiment ID {} and with ID {}", getSessionUser().getUsername(), experimentId, resourceId);
        return _resourceService.findByIdAndExperimentId(getSessionUser(), resourceId, experimentId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId));
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified project ID, subject ID and experimentId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId  wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read )
    public List<XnatAbstractresourceI> getByProjectIdAndSubjectIdExperimentId(
            @ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
            @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
            @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources with project ID {} , with subject ID {} and with experiment ID {} ", getSessionUser().getUsername(), projectId, subjectId, experimentId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId, experimentId), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either assessedId or scanId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatAbstractresourceI> getByExperimentIdAndScanId(@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
                                                                  @ApiParam(value = "The ID of the scan.") @PathVariable final String scanId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources with assessed ID {} and with scan ID {} s", getSessionUser().getUsername(), assessedId, scanId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByExperimentIdAndScanId(getSessionUser(), assessedId, scanId), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatAbstractresourceI> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws Exception {
        log.debug("User {} requested resources with project ID {} ", getSessionUser().getUsername(), projectId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByProjectId(getSessionUser(), projectId), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  Id and projectId", response = XnatAbstractresourceI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either projectId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatAbstractresourceI getByIdAndProjectId(@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                     @ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources with ID {} and with project ID {}", getSessionUser().getUsername(), resourceId, projectId);
        return _resourceService.findByIdAndProjectId(getSessionUser(), resourceId, projectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId));
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested subjectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatAbstractresourceI> getBySubject(@ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources with subject ID {}", getSessionUser().getUsername(), subjectId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findBySubjectId(getSessionUser(), subjectId), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresourceI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either subjectId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET, restrictTo = AccessLevel.Read )
    public XnatAbstractresourceI getByIdAndSubjectId(@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId,
                                                     @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) @Subject final String subjectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources wth ID {} and with subject ID {} ", getSessionUser().getUsername(), resourceId, subjectId);
        return _resourceService.findByIdAndSubjectId(getSessionUser(), resourceId, subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, subjectId));
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatAbstractresourceI> getByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                  @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources wth projectId {} and with subject ID{} ", getSessionUser().getUsername(), projectId, subjectId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresourceI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatAbstractresourceI getByIdAndProjectIdAndSubjectId(@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                                 @ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                 @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources wth project ID {} , with subject ID {} and with ID {} ", getSessionUser().getUsername(), projectId, subjectId, resourceId);
        return _resourceService.findByIdAndProjectIdAndSubjectId(getSessionUser(), resourceId, projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId));
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either experimentId or assessedId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/experiments/{assessedId}/assessors/{experimentId}/resources",
                                 "/experiments/{assessedId}/assessors/{experimentId}/{type}/resources"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatAbstractresourceI> getByExperimentIdAndAssessed(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                    @ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
                                                                    @ApiParam(value = "The type of resource.") @PathVariable(required = false) final String type) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources wth experiment ID {} and with assessed ID {} ", getSessionUser().getUsername(), experimentId, assessedId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByExperimentIdAndAssessedId(getSessionUser(), experimentId, assessedId, type), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresourceI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either experimentId or assessedId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/experiments/{assessedId}/assessors/{experimentId}/resources/{resourceId}",
                                 "/experiments/{experimentId}/assessors/{assessedId}/{type}/resources/{resourceId}"}, produces = MediaType.APPLICATION_XML_VALUE, method = GET)
    public XnatAbstractresourceI getByExperimentIdAndAssessedIdAndResourceId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                             @ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
                                                                             @ApiParam(value = "The type of resource") @PathVariable(required = false) final String type,
                                                                             @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources wth experiment ID {} , with assessed ID {} and with ID {} }", getSessionUser().getUsername(), experimentId, assessedId, resourceId);
        return _resourceService.findByExperimentIdAndAssessedIdAndResourceId(getSessionUser(), experimentId, assessedId, type, resourceId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, assessedId));
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either subjectId or experimentId or assessedId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/{type}/resources"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET,  restrictTo = AccessLevel.Read)
    public List<XnatAbstractresourceI> getByIdAndProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                                                   @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                                                                   @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                                                   @ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
                                                                                                   @ApiParam(value = "The type string.") @PathVariable(required = false) final String type) throws NotFoundException, DataFormatException {
        log.debug("User {} requested resources wth project ID {} , with subject ID {} , with experiment ID {} and with assessedId {} }", getSessionUser().getUsername(), projectId, subjectId, experimentId, assessedId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(getSessionUser(), projectId, subjectId, experimentId, assessedId, type), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresourceI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 404, message = "The requested resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET,  restrictTo = AccessLevel.Read)
    public List<XnatAbstractresourceI> getResourceByProjectAndSubjectAndExperimentAndScans(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) @Project final String projectId,
                                                                                           @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) @Subject final String subjectId,
                                                                                           @ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId,
                                                                                           @ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId) throws Exception {
        log.debug("User {} requested resources wth project ID {} , with subject ID {} , with assessed ID {} and with scan ID {} }", getSessionUser().getUsername(), projectId, subjectId, assessedId, scanId);
        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        return GenericUtils.convertToTypedList(_resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndScanId(getSessionUser(), projectId, subjectId, assessedId, scanId), XnatAbstractresourceI.class);
    }

    @ApiOperation(value = "Create a new resource", notes = "Creates the submitted resource.", response = XnatResourcecatalogI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created resource."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create resource"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources",
                                 "/subjects/{subjectId}/resources",
                                 "/experiments/{experimentId}/resources",
                                 "/experiments/{assessorId}/scans/{scanId}/resources",
                                 "/experiments/{assessorId}/assessors/{experimentId}/resources",
                                 "/experiments/{assessorId}/assessors/{experimentId}/{type}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST, restrictTo = AccessLevel.Read)
    @AuthDelegate(CreateProjectXapiAuthorization.class)
    public XnatResourcecatalogI createResource(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) @Project final String projectId,
                                               @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) @Subject final String subjectId,
                                               @ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) @Subject final String experimentId,
                                               @ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
                                               @ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
                                               @ApiParam(value = "The label of the resource.") @RequestParam(required = false) final String label,
                                               @ApiParam(value = "The label of the type.") @PathVariable(required = false) final String type,
                                               @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false) String eventReason,
                                               @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false) String eventId,
                                               @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false) String eventType,
                                               @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false) String eventAction,
                                               @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false) String eventComment,
                                               @ApiParam("The description value ") @RequestParam(name = "description", required = false) String description,
                                               @ApiParam("The format value ") @RequestParam(name = "format", required = false) String format,
                                               @ApiParam("The content value ") @RequestParam(name = "content", required = false) String content,
                                               @ApiParam("The tags value ") @RequestParam(name = "tags", required = false) String[] tags,
                                               @RequestBody final XnatResourceI xnatResource) throws DataFormatException, NotFoundException, ResourceAlreadyExistsException {
        log.debug("Creating  resource with project ID {}", projectId);

        if (StringUtils.isNotBlank(label) && !StringUtils.equals(xnatResource.getLabel(), label)) {
            throw new DataFormatException("You specified the label " + label + " in your request but the resource is assigned to project " + projectId + ". These values must be the same.");
        }

        // TODO: Remove convertToTypedList() wrapper when ResourceService interface is refactored to use interfaces instead of heavy XFT objects.
        // TODO: Also, there are more efficient ways to validate whether a resource exists within a project. Use that instead.
        final List<XnatAbstractresourceI> catalogs = GenericUtils.convertToTypedList(_resourceService.findByProjectIdAndLabel(getSessionUser(), projectId, label), XnatAbstractresourceI.class);
        if (!catalogs.isEmpty()) {
            throw new ResourceAlreadyExistsException("You specified the label in your request is already exists", label);
        }

        return _resourceService.create(getSessionUser(), projectId, subjectId, experimentId, assessorId, scanId, type, xnatResource, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment),
                                       description, format, content, tags);
    }

    @ApiOperation(value = "Delete an existing resource", notes = "Deletes the specified resource.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified resource."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified resource"),
                   @ApiResponse(code = 404, message = "The specified resource or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}",
                                 "/subjects/{subjectId}/resources/{resourceId}",
                                 "/experiments/{experimentId}/resources/{resourceId}",
                                 "/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}",
                                 "/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE,restrictTo = AccessLevel.Authorizer)
    public void deleteResource(@ApiParam("The ID of the project to be deleted") @PathVariable(required = false) @Project final String projectId,
                               @ApiParam("The ID of the subject to be deleted") @PathVariable(required = false) @Subject final String subjectId,
                               @ApiParam("The ID of the experiment to be deleted") @PathVariable(required = false) @Experiment final String experimentId,
                               @ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
                               @ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
                               @ApiParam(value = "The label of the type.") @PathVariable(required = false) final String type,
                               @ApiParam("The ID of the resource to be deleted") @PathVariable final String resourceId,
                               @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false) String eventReason,
                               @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false) String eventId,
                               @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false) String eventType,
                               @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false) String eventAction,
                               @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false) String eventComment) {
        log.debug("Deleting resource  with project ID {}", resourceId);
        _resourceService.delete(getSessionUser(), projectId, subjectId, experimentId, assessorId, scanId, type, resourceId, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = DicomDir.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/experiments/{experimentId}/DIR", "/projects/{projectId}/experiments/{experimentId}/DIR"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = GET)
    public List<DicomDir> getAllDIRResources(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) @Project final String projectId,
                                             @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                             @ApiParam(value = "The value  of the filepath.") @RequestParam(required = false) final String filepath,
                                             @ApiParam(value = "The value of the recursive.") @RequestParam(required = false) final boolean recursive,
                                             @ApiParam(value = "The value of the isXarReference.") @RequestParam(required = false) final boolean isXarReference) throws NotFoundException, DataFormatException, NotAuthenticatedException, InvalidFileCharacters {

        log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
        return _resourceService.findAllDIRResources(getSessionUser(), projectId, experimentId, filepath, recursive, isXarReference);
    }

    @ApiOperation(value = "Downloads the contents of the specified resource XAR.", response = StreamingResponseBody.class)
    @ApiResponses({@ApiResponse(code = 200, message = "The requested resources were successfully downloaded."),
                   @ApiResponse(code = 204, message = "No resources were specified."),
                   @ApiResponse(code = 400, message = "Something is wrong with the request format."),
                   @ApiResponse(code = 403, message = "The user is not authorized to access one or more of the specified resources."),
                   @ApiResponse(code = 404, message = "The request was valid but one or more of the specified resources was not found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/experiments/{experimentId}/XAR", "/projects/{projectId}/experiments/{experimentId}/XAR"}, produces = MediaTypeUtil.APPLICATION_XAR, method = RequestMethod.GET,restrictTo = AccessLevel.Read)
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> downloadXarResourceZip(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) @Project final String projectId,
                                                                        @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment  final String experimentId,
                                                                        @ApiParam(value = "The value  of the filepath.") @RequestParam(required = false) final String filepath,
                                                                        @ApiParam(value = "The value  of the recursive.") @RequestParam(required = false) final boolean recursive,
                                                                        @ApiParam(value = "The value  of the isXarReference.") @RequestParam(required = false) final boolean isXarReference,
                                                                        @ApiParam(value = "The value  of the compression.") @RequestParam(required = false) final String compression,
                                                                        @ApiParam(value = "The value  of the sRequest.") final HttpServletRequest sRequest,
                                                                        @ApiParam(value = "The value  of the hRequest.") @RequestHeader HttpHeaders hRequest) throws NotFoundException, NotAuthenticatedException, InitializationException, org.nrg.xnat.services.resources.impl.ResourceServiceImpl.InvalidFileCharacters {
        final UserI user = getSessionUser();

        StreamingResponseBody result = _resourceService.findAllXARResources(user, projectId, experimentId, filepath, recursive, isXarReference, sRequest, hRequest, compression);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaTypeUtil.APPLICATION_XAR)
                             .header(HttpHeaders.CONTENT_DISPOSITION, _resourceService.getContentDisposition())
                             .body(result);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ResourceFile> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
                                             @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                             @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException {
        log.debug("User {} requested  resource catalog with Project ID {}", getSessionUser().getUsername(), projectId);
        return _resourceService.findByProjectId(getSessionUser(), projectId, contents, formats);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested subjectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog  wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<ResourceFile> getBySubjectId(@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
                                             @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                             @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with subject ID {}", getSessionUser().getUsername(), subjectId);
        return _resourceService.findBySubjectId(getSessionUser(), subjectId, contents, formats);

    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ResourceFile> getByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                         @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                         @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                         @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with Project ID {} and subject ID {}", getSessionUser().getUsername(), projectId, subjectId);
        return _resourceService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId, contents, formats);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ResourceFile> getByProjectIdAndSubjectIdAndExperimentId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                        @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                                        @ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
                                                                        @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                                        @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with Project ID {} and subject ID {}", getSessionUser().getUsername(), projectId, subjectId);
        return _resourceService.findByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId, experimentId, contents, formats);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either projectId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ResourceFile> getByProjectIdAndResourceId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                          @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                          @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                          @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException {
        log.debug("User {} requested  resource catalog with Project ID {}", getSessionUser().getUsername(), projectId);
        return _resourceService.findByProjectIdAndResourceId(getSessionUser(), projectId, resourceId, contents, formats);

    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either subjectId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ResourceFile> getBySubjectIdAndResourceId(@ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                          @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                          @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                          @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with subject ID {} and resource ID {}", getSessionUser().getUsername(), subjectId, resourceId);
        return _resourceService.findBySubjectIdAndResourceId(getSessionUser(), subjectId, resourceId, contents, formats);

    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either experimentId or assessorId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET,restrictTo = AccessLevel.Read)
    public List<ResourceFile> getByExperimentIdAndAssessorId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                             @ApiParam(value = "The ID of the assessorId.") @PathVariable final String assessorId,
                                                             @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                             @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with experiment ID {} and with assessor ID {}", getSessionUser().getUsername(), experimentId, assessorId);
        return _resourceService.findByExperimentIdAndAssessorId(getSessionUser(), experimentId, assessorId, contents, formats);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested resource."),
                   @ApiResponse(code = 400, message = "The requested either experimentId or assessorId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ResourceFile> getByExperimentIdAndAssessorIdAndResourceId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                          @ApiParam(value = "The ID of the assessorId.") @PathVariable final String assessorId,
                                                                          @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                                          @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                                          @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with experiment ID {} and with assessor ID {} and with resource ID {}", getSessionUser().getUsername(), experimentId, assessorId, resourceId);
        return _resourceService.findByExperimentIdAndAssessorIdAndResourceId(getSessionUser(), experimentId, assessorId, resourceId, contents, formats);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  files with the specified  projectId and subjectId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested files."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or assessorId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessedId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ResourceFile> getByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                                                     @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId,
                                                                                     @ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                                                     @ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
                                                                                     @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                                                     @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with project ID {}, with subject ID {} , with experiment ID {} and with assessed ID {} ", getSessionUser().getUsername(), projectId, subjectId, experimentId, assessedId);
        return _resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(getSessionUser(), projectId, subjectId, experimentId, assessedId, contents, formats);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested files."),
                   @ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<ResourceFile> getByExperiment(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                              @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                              @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with experiment ID {}", getSessionUser().getUsername(), experimentId);
        return _resourceService.findByExperimentId(getSessionUser(), experimentId, contents, formats);
    }

    @ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = ResourceFile.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested files."),
                   @ApiResponse(code = 400, message = "The requested either experimentId or resourceId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read )
    public List<ResourceFile> getByExperimentIdAndResourceId(@ApiParam(value = "The ID of the experiment.") @PathVariable @Experiment final String experimentId,
                                                             @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                             @ApiParam(value = "The values of the contents.") @RequestParam(name = "contents", required = false) final String[] contents,
                                                             @ApiParam(value = "The values of the formats.") @RequestParam(name = "formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
        log.debug("User {} requested  resource catalog with experiment ID {} and resource ID {} ", getSessionUser().getUsername(), experimentId, resourceId);
        return _resourceService.findByExperimentIdAndResourceId(getSessionUser(), experimentId, resourceId, contents, formats);
    }

    @ApiOperation(value = "Delete an existing resource file", notes = "Deletes the specified resource file.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified resource file."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete resource file in the specified resource file"),
                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}/files"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE,restrictTo = AccessLevel.Edit)
    public void deleteFile(@ApiParam("The ID of the resource file to be deleted") @PathVariable(required = false) @Project final String projectId,
                           @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) @Subject final String subjectId,
                           @ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) @Experiment final String experimentId,
                           @ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
                           @ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
                           @ApiParam(value = "The label of the type.") @PathVariable(required = false) final String type,
                           @ApiParam("The ID of the project") @PathVariable(required = false) final String resourceId,
                           @ApiParam("The remove Files  value ") @RequestParam(name = "removeFiles", required = false) boolean removeFiles,
                           @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false) String eventReason,
                           @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false) String eventId,
                           @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false) String eventType,
                           @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false) String eventAction,
                           @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false) String eventComment) throws Exception {
        log.debug("Deleting resource file with project ID {}", projectId);
        _resourceService.deleteResourceFile(getSessionUser(), projectId, subjectId, experimentId, assessorId, scanId, type, resourceId, removeFiles, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    @ApiOperation(value = "Create a new resource file", notes = "Creates the submitted resource file.", response = Integer.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}/files", "/subjects/{subjectId}/resources/{resourceId}/files",
                                 "/experiments/{experimentId}/resources/{resourceId}/files", "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources/{resourceId}/files",
                                 "/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}/files",
                                 "/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}/files",
                                 "/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}/files",
                                 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}/files"},
                        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST, restrictTo = AccessLevel.Edit)
    public Integer createResourceFile(@ApiParam("The resource file to be created.") @RequestParam MultipartFile file,
                                      @ApiParam("The ID of the project.") @PathVariable(required = false) @Project final String projectId,
                                      @ApiParam("The ID of the subject.") @PathVariable(required = false) @Subject final String subjectId,
                                      @ApiParam("The ID of the experiment.") @PathVariable(required = false) @Experiment  final String experimentId,
                                      @ApiParam("The ID of the assessor.") @PathVariable(required = false) final String assessorId,
                                      @ApiParam("The ID of the scan.") @PathVariable(required = false) final String scanId,
                                      @ApiParam("The ID of the type") @PathVariable(required = false) final String type,
                                      @ApiParam("The ID of the project") @PathVariable(required = false) final String resourceId,
                                      @ApiParam("The file rename.") @RequestParam(name = "rename", required = false) final String requestRename,
                                      @ApiParam("The file description.") @RequestParam(name = "description", required = false) final String requestDesc,
                                      @ApiParam("The file format.") @RequestParam(name = "format", required = false) final String requestFormat,
                                      @ApiParam("The file content.") @RequestParam(name = "content", required = false) final String requestContent,
                                      @ApiParam("The file tags.") @RequestParam(name = "tags", required = false) final List<String> requestTags,
                                      @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false) String eventReason,
                                      @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false) String eventId,
                                      @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false) String eventType,
                                      @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false) String eventAction,
                                      @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false) String eventComment)
            throws Exception {
        log.debug("Creating resource file with project ID: {}", projectId);

        XnatResourceInfo xnatResourceInfo = getXnatResourceInfo(requestContent, requestFormat, requestTags, requestDesc, requestRename, file);

        return _resourceService.createResourceFile(getSessionUser(), xnatResourceInfo, projectId, subjectId, experimentId, assessorId, scanId, type, resourceId, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment));
    }

    @ApiOperation(value = "Refresh the specified catalog")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/services/refresh/catalog", produces = MediaType.APPLICATION_JSON_VALUE, method = POST)
    public void createCatalogRefresh(@ApiParam(value = "The value of the resources.") @RequestParam(name = "resources") final List<String> resources,
                                     @ApiParam(value = "The value of the append.") @RequestParam(name = "append", required = false, defaultValue = "false") final boolean append,
                                     @ApiParam(value = "The value of the checksum.") @RequestParam(name = "checksum", required = false, defaultValue = "false") final boolean checksum,
                                     @ApiParam(value = "The value of the delete.") @RequestParam(name = "delete", required = false, defaultValue = "false") final boolean delete,
                                     @ApiParam(value = "The value of the populateStats.") @RequestParam(name = "populateStats", required = false, defaultValue = "false") final boolean populateStats,
                                     @ApiParam(value = "The value of the options.") @RequestParam(name = "options") List<String> options) throws NotFoundException, DataFormatException, ClientException, ServerException {
        log.debug("User {} requested resources {}", getSessionUser().getUsername(), resources);
        _resourceService.createCatalogRefresh(getSessionUser(), resources, append, checksum, delete, populateStats, options);
    }

    @ApiOperation(value = "Gets the All Triage resource", notes = "Returns the  Triage resource", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Triage resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/services/triage/projects/{projectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET,restrictTo = AccessLevel.Read)
    public List<TriageEntry> getAll(@ApiParam("The ID of the project ") @PathVariable @Project final String projectId,
                                    @ApiParam("The value of Http Servlet request") HttpServletRequest request) throws NotFoundException, DataFormatException, InsufficientPrivilegesException, InitializationException {
        log.debug("User {} requested Triage resource", getSessionUser().getUsername());
        return _resourceService.findTriageByProjectId(getSessionUser(), projectId, request);
    }

    @ApiOperation(value = "create Triage resource", notes = " creating the Triage resource", response = void.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested  Triage resource."),
                   @ApiResponse(code = 400, message = "The requested  Triage resource wasn't found."),
                   @ApiResponse(code = 404, message = "The requested  Triage resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/services/triage/projects/{projectId}/resources",
                                 "/services/triage/projects/{projectId}/resources/{xName}",
                                 "/services/triage/projects/{projectId}/resources/{xName}/files",
                                 "/services/triage/projects/{projectId}/resources/{xName}/files/{file}"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST, restrictTo = AccessLevel.Edit)
    public void createTriage(@ApiParam("The ID of the project ") @PathVariable @Project final String projectId,
                             @ApiParam("The value of xName") @PathVariable(required = false) final String xName,
                             @ApiParam("The value of file") @PathVariable(required = false) final String file,
                             @ApiParam("The value of eventReason") @RequestParam(name = "event_reason", required = false) final String eventReason,
                             @ApiParam("The value of eventComment") @RequestParam(required = false) final String eventComment,
                             @ApiParam("The value of eventId") @RequestParam(required = false) final String eventId,
                             @ApiParam("The value of target") @RequestParam(required = false) final String target,
                             @ApiParam("Indicates whether the request object is in the request body") @RequestParam(required = false) final boolean inbody,
                             @ApiParam("The value of overwrite") @RequestParam(required = false) final String overwrite,
                             @ApiParam("The value of format") @RequestParam(required = false) final String format,
                             @ApiParam("The value of content") @RequestParam(required = false) final String content,
                             @ApiParam("The value of extract") @RequestParam(required = false) final String extract,
                             @ApiParam("The value of Http Servlet request") HttpServletRequest request) throws InitializationException, DataFormatException {
        log.debug("User {} requested Study Routing", getSessionUser().getUsername());
        _resourceService.create(getSessionUser(), projectId, xName, file, eventReason, eventComment, eventId, target, inbody, overwrite, format, content, extract, request);
    }

    @ApiOperation(value = "delete  Triage resource", notes = " delete the  Triage resource ", response = void.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested  Triage resource."),
                   @ApiResponse(code = 400, message = "The requested  Triage resource wasn't found."),
                   @ApiResponse(code = 404, message = "The requested  Triage resource wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/services/triage/projects/{projectId}/resources",
                                 "/services/triage/projects/{projectId}/resources/{xName}",
                                 "/services/triage/projects/{projectId}/resources/{xName}/files",
                                 "/services/triage/projects/{projectId}/resources/{xName}/files/{file}"},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE, restrictTo = AccessLevel.Delete)
    public void deleteStudyRouting(@ApiParam("The ID of the project ") @PathVariable @Project final String projectId,
                                   @ApiParam("The value of xName") @PathVariable(required = false) final String xName,
                                   @ApiParam("The value of file") @PathVariable(required = false) final String file,
                                   @ApiParam("The value of xName") @RequestParam(required = false) final String eventReason,
                                   @ApiParam("The value of xName") @RequestParam(required = false) final String eventComment,
                                   @ApiParam("The value of xName") @RequestParam(required = false) final String eventId) {
        log.debug("User {} requested Study Routing", getSessionUser().getUsername());
        _resourceService.deleteTriage(getSessionUser(), projectId, xName, file, eventReason, eventComment, eventId);
    }

    /**
     * Gets the information for the requested resource.
     *
     * @param content     The resource content to match.
     * @param format      The resource format to match.
     * @param tags        One or more resource tags to match.
     * @param description The resource description to match.
     * @param rename      The target name for the resource.
     * @param file        The multipart file for the resource.
     *
     * @return A new {@link XnatResourceInfo resource info model object} for the requested resource.
     *
     * @throws IOException When an error occurs working with the multipart file.
     */
    private XnatResourceInfo getXnatResourceInfo(String content, String format, List<String> tags, String description, String rename, MultipartFile file) throws IOException {
        return XnatResourceInfo.builder()
                               .username(getSessionUser().getUsername())
                               .created(new Date())
                               .content(content)
                               .format(format)
                               .tags(tags)
                               .description(description)
                               .name(file.getOriginalFilename())
                               .fileSize(file.getSize())
                               .rename(rename)
                               .multipartFile(file).build();
    }

    private final ResourceService _resourceService;
}
