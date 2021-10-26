package org.nrg.xapi.rest.subjects;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.Subject;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatSubjectdataI;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.subjects.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api("XNAT subject Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class SubjectApi extends AbstractXapiProjectRestController {
    @Autowired
    public SubjectApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final SubjectService subjectService) {
        super(userManagementService, roleHolder);
        _subjectService = subjectService;
    }

    @ApiOperation(value = "Gets the requested  subject", notes = "Returns the  subject with the specified ID", response = XnatSubjectdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested subject."),
                   @ApiResponse(code = 400, message = "The requested subjectId wasn't found."),
                   @ApiResponse(code = 403, message = "The user has insufficient privileges to access the requested subject."),
                   @ApiResponse(code = 404, message = "The requested subject wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/subjects/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatSubjectdataI getBySubjectId(@ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId) throws InsufficientPrivilegesException, NotFoundException, DataFormatException {
        log.debug("User {} requested subject with ID {}", getSessionUser().getUsername(), subjectId);
        return _subjectService.findById(getSessionUser(), subjectId).orElseThrow(() -> new NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME, subjectId));
    }

    @ApiOperation(value = "Get list of subjects", notes = "The subjects function returns a list of all subjects configured in the XNAT system.", response = XnatSubjectdataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured subjects."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested subject wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/subjects", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatSubjectdataI> getAllSubjects() throws NotFoundException {
        log.debug("User {} requested subject", getSessionUser().getUsername());
        return _subjectService.findAll(getSessionUser());
    }

    @ApiOperation(value = "Get list of subjects", notes = "The subjects function returns a list of all subjects configured in the XNAT system.", response = XnatSubjectdataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured subjects."),
                   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested subject wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public List<XnatSubjectdataI> getAllByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
        return _subjectService.findAllByProjectId(getSessionUser(), projectId);
    }

    @ApiOperation(value = "Get list of subjects", notes = "The subjects function returns a list of all subjects configured in the XNAT system.", response = XnatSubjectdataI.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured subjects."),
                   @ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested subject wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET, restrictTo = AccessLevel.Read)
    public XnatSubjectdataI getAllByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable @Project final String projectId,
                                                          @ApiParam(value = "The ID of the subject.") @PathVariable @Subject final String subjectId) throws NotFoundException, DataFormatException {
        log.debug("User {} requested project with ID {} and subject with ID {}", getSessionUser().getUsername(), projectId, subjectId);
        return _subjectService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME, projectId));
    }

    @ApiOperation(value = "Create a new subject", notes = "Creates the submitted subject.", response = XnatSubjectdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created subject."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create subjects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects", "/subjects"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST, restrictTo = AccessLevel.Edit)
    public XnatSubjectdataI createSubject(@ApiParam("The project in which the subject should be created") @PathVariable @Project final String projectId,
                                          @ApiParam("The subject to be created.") @RequestBody final XnatSubjectdataI subject,
                                          @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                          @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                          @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                          @ApiParam("The event  action value ") @RequestParam(defaultValue = "Added Subject") String eventAction,
                                          @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws Exception {
        log.debug("User {} requested to create subject with ID {}", getSessionUser().getUsername(), subject.getId());
        final boolean subjectHasProject = StringUtils.isNotBlank(subject.getProject());
        final boolean hasProject        = StringUtils.isNotBlank(projectId);
        if (!subjectHasProject && !hasProject) {
            throw new DataFormatException("You must specify a project in which the subject should be created.");
        }
        if (subjectHasProject && hasProject && !StringUtils.equals(subject.getProject(), projectId)) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the subject is assigned to project " + subject.getProject() + ". These values must be the same.");
        }
        if (!subjectHasProject) {
            subject.setProject(projectId);
        }
        return _subjectService.create(getSessionUser(), subject, XnatEventUtil.getXnatEventUtil(eventType, eventReason, eventId, eventAction, eventComment));
    }

    @ApiOperation(value = "Update an existing subject", notes = "Updates the submitted subject.", response = XnatSubjectdataI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated subject."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit subjects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}", "/subjects/{subjectId}"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = PUT, restrictTo = AccessLevel.Edit)
    public XnatSubjectdataI updateSubject(@ApiParam("The project containing the subject to be updated") @PathVariable @Project final String projectId,
                                          @ApiParam("The ID of the subject to be updated") @PathVariable @Subject final String subjectId,
                                          @ApiParam("The subject to be updated.") @RequestBody final XnatSubjectdataI subject,
                                          @ApiParam("The label value.") @RequestParam(required = false) String label,
                                          @ApiParam("The primary value.") @RequestParam(defaultValue = "false") boolean primary,
                                          @ApiParam("The gender value.") @RequestParam(required = false) String gender,
                                          @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                                          @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                                          @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                                          @ApiParam("The event  action value ") @RequestParam(required = false) String eventAction,
                                          @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws Exception {
        log.debug("User {} requested to update subject with ID {}", getSessionUser().getUsername(), subjectId);
        if (StringUtils.isNotBlank(projectId) && !StringUtils.equals(subject.getProject(), projectId)) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the subject is assigned to project " + subject.getProject() + ". These values must be the same.");
        }
        if (!StringUtils.equals(subjectId, subject.getId())) {
            throw new DataFormatException("You specified the subject ID " + subjectId + " in your request but the subject to be updated has the ID " + subject.getId() + ". These values must be the same.");
        }
        log.debug("Controller Api- Update subject {} (ID {}) in project {}", subject.getLabel(), subjectId, subject.getProject());
        return _subjectService.update(getSessionUser(), subject, label, primary, gender, XnatEventUtil.getXnatEventUtil(eventType, eventReason, eventId, eventAction, eventComment));
    }

    @ApiOperation(value = "Delete an existing subject", notes = "Deletes the specified subject.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified subject."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete subjects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project or subject doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}", "/subjects/{subjectId}"},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = DELETE, restrictTo = AccessLevel.Delete)
    public void deleteSubject(@ApiParam("The project containing the subject to be deleted") @PathVariable @Project final String projectId,
                              @ApiParam("The ID of the subject to be deleted") @PathVariable @Subject final String subjectId,
                              @ApiParam("The removeFiles value ") @RequestParam(defaultValue = "false") boolean removeFiles,
                              @ApiParam("The event reason  value ") @RequestParam(required = false) String eventReason,
                              @ApiParam("The event id value ") @RequestParam(required = false) String eventId,
                              @ApiParam("The event type value ") @RequestParam(required = false) String eventType,
                              @ApiParam("The event  action value ") @RequestParam(defaultValue = "Deleted") String eventAction,
                              @ApiParam("The event comment value ") @RequestParam(required = false) String eventComment) throws ClientException, DataFormatException, NotFoundException, InitializationException, InsufficientPrivilegesException {
        if (StringUtils.isBlank(projectId)) {
            log.debug("User {} requested to delete subject with ID {}", getSessionUser().getUsername(), subjectId);
            _subjectService.deleteById(getSessionUser(), subjectId, removeFiles, new XnatEventUtil(eventType, eventReason, eventId, eventAction, eventComment));
        } else {
            log.debug("User {} requested to delete subject {} in project {}", getSessionUser().getUsername(), subjectId, projectId);
            _subjectService.deleteById(getSessionUser(), _subjectService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME, projectId + ":" + subjectId)).getId(), removeFiles, new XnatEventUtil(eventType, eventReason, eventId, eventAction, eventComment));
        }
    }

    private final SubjectService _subjectService;
}
