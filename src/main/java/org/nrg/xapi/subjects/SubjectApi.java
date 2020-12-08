package org.nrg.xapi.subjects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.subjects.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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

    @ApiOperation(value = "Gets the requested  subject", notes = "Returns the  subject with the specified ID", response = XnatSubjectdata.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested subject."),
                   @ApiResponse(code = 404, message = "The requested subject wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/subjects/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<XnatSubjectdata> getSubjectBySubjectId(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId) throws Exception {
        log.debug("Controller Api- get subjects by subjectId");
        XnatSubjectdata xnatSubject = _subjectService.findById(getSessionUser(), subjectId);
        if (xnatSubject == null) {
            throw new NotFoundException("No Subject with ID was found.");
        }
        return new ResponseEntity<>(xnatSubject, HttpStatus.OK);
    }

    @ApiOperation(value = "Get list of subjects", notes = "The subjects function returns a list of all subjects configured in the XNAT system.", response = XnatSubjectdata.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured subjects."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/subjects", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<List<XnatSubjectdata>> getAllSubjectList() throws Exception {
        log.debug("Controller Api- get subjects");
        List<XnatSubjectdata> xnatSubjects = _subjectService.getAll(getSessionUser());
        if (xnatSubjects == null) {
            throw new NotFoundException("No Subject with data was found.");
        }
        return new ResponseEntity<>(xnatSubjects, HttpStatus.OK);
    }


    @ApiOperation(value = "Get list of subjects", notes = "The subjects function returns a list of all subjects configured in the XNAT system.", response = XnatSubjectdata.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured subjects."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<List<XnatSubjectdata>> getAllSubjectListByProjectId(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String projectId) throws Exception {
        log.debug("Controller Api- get subjects");
        List<XnatSubjectdata> xnatSubjects = _subjectService.findByProject(getSessionUser(), projectId);
        if (xnatSubjects == null) {
            throw new NotFoundException("No Subject with data was found.");
        }
        return new ResponseEntity<>(xnatSubjects, HttpStatus.OK);
    }

    @ApiOperation(value = "Get list of subjects", notes = "The subjects function returns a list of all subjects configured in the XNAT system.", response = XnatSubjectdata.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured subjects."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<XnatSubjectdata> getAllSubjectListByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                                                                    @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId) throws Exception {
        log.debug("Controller Api- get subjects");
        XnatSubjectdata xnatSubject = _subjectService.findByProjectAndSubject(getSessionUser(), projectId, subjectId);
        if (xnatSubject == null) {
            throw new NotFoundException("No Subject with data was found.");
        }
        return new ResponseEntity<>(xnatSubject, HttpStatus.OK);
    }

    @ApiOperation(value = "Create a new subject", notes = "Creates the submitted subject.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created subject."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create subjects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects", "/subjects"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST)
    public XnatSubjectdata createSubject(@ApiParam("The project in which the subject should be created") @PathVariable(required = false) final String projectId,
                                         @ApiParam("The subject to be created.") @RequestBody final XnatSubjectdata subject) throws Exception {
        log.debug("Controller Api- Create subject: {}", subject);
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
        return _subjectService.create(getSessionUser(), subject);
    }

    @ApiOperation(value = "Update an existing subject", notes = "Updates the submitted subject.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated subject."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit subjects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}", "/subjects/{subjectId}"},
                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST)
    public XnatSubjectdata updateSubject(@ApiParam("The project containing the subject to be updated") @PathVariable(required = false) final String projectId,
                                         @ApiParam("The ID of the subject to be updated") @PathVariable final String subjectId,
                                         @ApiParam("The subject to be updated.") @RequestBody final XnatSubjectdata subject) throws Exception {
        if (StringUtils.isNotBlank(projectId) && !StringUtils.equals(subject.getProject(), projectId)) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the subject is assigned to project " + subject.getProject() + ". These values must be the same.");
        }
        if (!StringUtils.equals(subjectId, subject.getId())) {
            throw new DataFormatException("You specified the subject ID " + subjectId + " in your request but the subject to be updated has the ID " + subject.getId() + ". These values must be the same.");
        }
        log.debug("Controller Api- Update subject {} (ID {}) in project {}", subject.getLabel(), subjectId, subject.getProject());
        return _subjectService.update(getSessionUser(), subject);
    }

    @ApiOperation(value = "Delete an existing subject", notes = "Deletes the specified subject.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified subject."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete subjects in the specified project"),
                   @ApiResponse(code = 404, message = "The specified project or subject doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}", "/subjects/{subjectId}"},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST)
    public void deleteSubject(@ApiParam("The project containing the subject to be deleted") @PathVariable(required = false) final String projectId,
                              @ApiParam("The ID of the subject to be deleted") @PathVariable final String subjectId) throws Exception {
        // TODO: Would be good to validate subject ID/label with project ID
        log.debug("Controller Api- Delete subject {} in project {}", subjectId, StringUtils.defaultIfBlank(projectId, "N/A"));
        _subjectService.deleteById(getSessionUser(), subjectId);
    }

    private final SubjectService _subjectService;
}
