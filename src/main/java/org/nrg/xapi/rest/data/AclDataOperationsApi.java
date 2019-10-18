package org.nrg.xapi.rest.data;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.*;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.utils.SaveItemHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.nrg.xft.event.EventUtils.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api("ACL Data Operations API")
@XapiRestController
@RequestMapping(value = "/acl/data")
@Getter(PRIVATE)
@Accessors(prefix = "_")
@Slf4j
public class AclDataOperationsApi extends AbstractXapiRestController {
    @Autowired
    public AclDataOperationsApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final NamedParameterJdbcTemplate template) {
        super(userManagementService, roleHolder);
        _template = template;
        _helper = new SaveItemHelper();
    }

    @ApiOperation(value = "Gets a list of IDs of experiments accessible to the current user.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of experiments."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveExperiments() {
        // TODO: Dummy implementation: doesn't filter by user access, could use @PostFilter
        return getTemplate().queryForList("SELECT id FROM xnat_experimentdata", EmptySqlParameterSource.INSTANCE, String.class);
    }

    @ApiOperation(value = "Creates a new experiment.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully created the experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE, method = POST)
    @ResponseBody
    public XnatExperimentdata createExperiment(final @RequestBody XnatExperimentdata experiment) throws Exception {
        return createExperiment(experiment, "Created new experiment " + experiment.getLabel() + " in project " + experiment.getProject(), "");
    }

    @ApiOperation(value = "Creates a new experiment.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully created the experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE, method = POST)
    @ResponseBody
    public XnatExperimentdata createExperiment(final @RequestParam XnatExperimentdata experiment, final @RequestParam(required = false) String reason, final @RequestParam(required = false) String comment) throws Exception {
        // TODO: Dummy implementation: doesn't filter by user access, could use @PostFilter
        if (StringUtils.isNotBlank(experiment.getId())) {
            throw new DataFormatException("You can't create an experiment if it already has an ID.");
        }
        final String newId = XnatExperimentdata.CreateNewID();
        experiment.setId(newId);
        SaveItemHelper.authorizedSave(experiment, getSessionUser(), false, false, false, true, newEventInstance(reason, comment));
        return XnatExperimentdata.getXnatExperimentdatasById(newId, getSessionUser(), false);
    }

    @ApiOperation(value = "Gets the experiment with the specified ID.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified experiment."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatExperimentdata retrieveExperiment(final @PathVariable String experimentId) {
        // TODO: Dummy implementation: doesn't check experiment access
        return XnatExperimentdata.getXnatExperimentdatasById(experimentId, getSessionUser(), false);
    }

    @ApiOperation(value = "Updates the experiment with the specified ID.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified experiment."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE, method = PUT)
    @ResponseBody
    public XnatExperimentdata updateExperiment(final @PathVariable String experimentId, final @RequestBody XnatExperimentdata experiment) throws Exception {
        // TODO: Dummy implementation: doesn't check experiment access
        if (!StringUtils.equalsIgnoreCase(experimentId, experiment.getId())) {
            throw new DataFormatException("The experiment ID does not match the ID of the submitted experiment");
        }
        SaveItemHelper.authorizedSave(experiment, getSessionUser(), false, false, false, true, newEventInstance("Updated experiment " + experimentId, ""));
        return XnatExperimentdata.getXnatExperimentdatasById(experimentId, getSessionUser(), false);
    }

    @ApiOperation(value = "Deletes the experiment with the specified ID.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully deleted the requested experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified experiment."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}", method = DELETE)
    public void deleteExperiment(final @PathVariable String experimentId) {
        // TODO: Dummy implementation: doesn't check experiment access
        final XnatExperimentdata experiment = retrieveExperiment(experimentId);
        experiment.delete(experiment.getPrimaryProject(false), getSessionUser(), true, DEFAULT_EVENT(getSessionUser(), "Deleted experiment " + experimentId));
    }

    @ApiOperation(value = "Gets the IDs of the assessors associated with the experiment with the specified ID.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of assessor IDs."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}/assessors", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveExperimentAssessors(final @PathVariable String experimentId) {
        // TODO: Dummy implementation: doesn't check experiment or assessor access
        return getTemplate().queryForList("SELECT id FROM xnat_imageassessordata WHERE imagesession_id = :experiment", new MapSqlParameterSource("experiment", experimentId), String.class);
    }

    @ApiOperation(value = "Creates a new assessor for the specified experiment.", response = XnatImageassessordata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully created the new assessor."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}/assessors", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE, method = POST)
    @ResponseBody
    public XnatImageassessordata createExperimentAssessor(final @PathVariable String experimentId, final @RequestParam XnatImageassessordata assessor) throws Exception {
        // TODO: Dummy implementation: doesn't filter by user access, could use @PostFilter
        if (StringUtils.isNotBlank(assessor.getId())) {
            throw new DataFormatException("You can't create an experiment if it already has an ID.");
        }
        final XnatExperimentdata experiment = retrieveExperiment(experimentId);
        if (experiment == null) {
            throw new NotFoundException("Image session " + experimentId + " does not exist.");
        }
        if (!(experiment instanceof XnatImagesessiondata)) {
            throw new DataFormatException("The experiment " + experimentId + " is not an image session.");
        }
        final String newId = XnatImagesessiondata.CreateNewID();
        assessor.setId(newId);
        assessor.setImagesessionId(experimentId);
        SaveItemHelper.authorizedSave(assessor, getSessionUser(), false, false, false, true, newEventInstance("Created new assessor for image session " + experimentId, ""));
        return XnatImageassessordata.getXnatImageassessordatasById(newId, getSessionUser(), false);
    }

    @ApiOperation(value = "Gets the assessor with the indicated ID or label associated with the indicated experiment.", response = XnatImageassessordata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}/assessors/{assessorId}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatImageassessordata retrieveExperimentAssessor(final @PathVariable String experimentId, final @PathVariable String assessorId) throws NotFoundException {
        // TODO: Dummy implementation: doesn't check experiment or assessor access
        try {
            final String resolvedId = getTemplate().queryForObject("SELECT id FROM xnat_imageassessordata a LEFT JOIN xnat_experimentdata x ON a.id = x.id WHERE a.imagesession_id = :experiment AND x.id = :assessor OR x.label = :assessor", new MapSqlParameterSource("experiment", experimentId).addValue("assessor", assessorId), String.class);
            return XnatImageassessordata.getXnatImageassessordatasById(resolvedId, getSessionUser(), false);
        } catch (DataAccessException e) {
            throw new NotFoundException("Couldn't find experiment " + experimentId + " and assessor " + assessorId);
        }
    }

    @ApiOperation(value = "Updates the assessor with the indicated ID or label associated with the indicated experiment.", response = XnatImageassessordata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully updated the assessor."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}/assessors/{assessorId}", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE, method = PUT)
    @ResponseBody
    public XnatImageassessordata updateExperimentAssessor(final @PathVariable String experimentId, final @PathVariable String assessorId, final @RequestBody XnatImageassessordata assessor) throws Exception {
        // TODO: Dummy implementation: doesn't check experiment or assessor access
        validateExperimentAssessor(experimentId, assessorId, assessor);
        SaveItemHelper.authorizedSave(assessor, getSessionUser(), false, false, false, true, newEventInstance("Updated assessor " + assessorId + " for image session " + experimentId, ""));
        final CriteriaCollection criteria = new CriteriaCollection("AND");
        criteria.addClause("label", assessor.getLabel());
        criteria.addClause("project", assessor.getProject());
        return XnatImageassessordata.getXnatImageassessordatasByField(criteria, getSessionUser(), false).get(0);
    }

    @ApiOperation(value = "Deletes the assessor with the indicated ID or label associated with the indicated experiment.")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully deleted the assessor."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experimentId}/assessors/{assessorId}", method = DELETE)
    @ResponseBody
    public void deleteExperimentAssessor(final @PathVariable String experimentId, final @PathVariable String assessorId) throws Exception {
        // TODO: Dummy implementation: doesn't check experiment or assessor access
        final XnatImageassessordata assessor = retrieveExperimentAssessor(experimentId, assessorId);
        validateExperimentAssessor(experimentId, assessorId, assessor);
        assessor.delete(assessor.getPrimaryProject(false), getSessionUser(), true, DEFAULT_EVENT(getSessionUser(), "Deleted image assessor " + assessorId));
    }

    @ApiOperation(value = "Gets the IDs of the projects accessible by the current user.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of accessible project IDs."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveProjects() {
        // TODO: Dummy implementation: doesn't filter by user access, could use @PostFilter
        return getTemplate().queryForList("SELECT id FROM xnat_projectdata", EmptySqlParameterSource.INSTANCE, String.class);
    }

    @ApiOperation(value = "Creates a new project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully created the project."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE, method = POST)
    @ResponseBody
    public XnatProjectdata createProject(final @RequestBody XnatProjectdata project) throws Exception {
        return createProject(project, "Created new project " + project.getName(), "");
    }

    @ApiOperation(value = "Creates a new project.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully created the project."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE, method = POST)
    @ResponseBody
    public XnatProjectdata createProject(final @RequestParam XnatProjectdata project, final @RequestParam(required = false) String reason, final @RequestParam(required = false) String comment) throws Exception {
        // TODO: Dummy implementation: doesn't filter by user access, could use @PostFilter
        final String projectId = project.getId();
        if (StringUtils.isBlank(projectId)) {
            throw new DataFormatException("You can't create an project without an ID.");
        }
        SaveItemHelper.authorizedSave(project, getSessionUser(), false, false, false, true, newEventInstance(reason, comment));
        return XnatProjectdata.getXnatProjectdatasById(projectId, getSessionUser(), false);
    }

    @ApiOperation(value = "Gets the project with the specified ID.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatProjectdata retrieveProject(final @PathVariable String project) {
        // TODO: Dummy implementation: mostly fine but doesn't check permissions at all, so good with @PreAuthorize on project ID
        return XnatProjectdata.getProjectByIDorAlias(project, getSessionUser(), false);
    }

    @ApiOperation(value = "Updates the project with the specified ID.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested project."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified project."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{projectId}", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE, method = PUT)
    @ResponseBody
    public XnatProjectdata updateProject(final @PathVariable String projectId, final @RequestBody XnatProjectdata project) throws Exception {
        // TODO: Dummy implementation: doesn't check project access
        if (!StringUtils.equalsIgnoreCase(projectId, project.getId())) {
            throw new DataFormatException("The project ID does not match the ID of the submitted project");
        }
        SaveItemHelper.authorizedSave(project, getSessionUser(), false, false, false, true, newEventInstance("Updated project " + projectId, ""));
        return XnatProjectdata.getXnatProjectdatasById(projectId, getSessionUser(), false);
    }

    @ApiOperation(value = "Deletes the project with the specified ID.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully deleted the requested project."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified project."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{projectId}", method = DELETE)
    public void deleteProject(final @PathVariable String projectId) throws Exception {
        // TODO: Dummy implementation: doesn't check project access
        final XnatProjectdata project = retrieveProject(projectId);
        project.delete(true, getSessionUser(), DEFAULT_EVENT(getSessionUser(), "Deleted project " + projectId));
    }

    @ApiOperation(value = "Creates a new subject.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully created the subject."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subjects", consumes = APPLICATION_XML_VALUE, produces = APPLICATION_XML_VALUE, method = POST)
    @ResponseBody
    public XnatSubjectdata createSubject(final @RequestBody XnatSubjectdata subject) throws Exception {
        return createSubject(subject, "Created new subject " + subject.getLabel() + " in project " + subject.getProject(), "");
    }

    @ApiOperation(value = "Creates a new subject.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully created the subject."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subjects", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_XML_VALUE, method = POST)
    @ResponseBody
    public XnatSubjectdata createSubject(final @RequestParam XnatSubjectdata subject, final @RequestParam(required = false) String reason, final @RequestParam(required = false) String comment) throws Exception {
        // TODO: Dummy implementation: doesn't filter by user access, could use @PostFilter
        if (StringUtils.isNotBlank(subject.getId())) {
            throw new DataFormatException("You can't create an subject if it already has an ID.");
        }
        if (StringUtils.isBlank(subject.getProject())) {
            throw new DataFormatException("You can't create an subject without a project ID.");
        }
        final String newId = XnatSubjectdata.CreateNewID();
        subject.setId(newId);
        SaveItemHelper.authorizedSave(subject, getSessionUser(), false, false, false, true, newEventInstance(reason, comment));
        return XnatSubjectdata.getXnatSubjectdatasById(newId, getSessionUser(), false);
    }

    @ApiOperation(value = "Deletes the subject with the specified ID.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully deleted the requested subject."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified subject."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subjects/{subjectId}", method = DELETE)
    public void deleteSubject(final @PathVariable String subjectId) throws Exception {
        // TODO: Dummy implementation: doesn't check subject access
        final XnatSubjectdata subject = retrieveSubject(subjectId);
        if (subject == null) {
            throw new NotFoundException("Couldn't find subject with ID " + subjectId);
        }
        subject.delete(subject.getPrimaryProject(false), getSessionUser(), true, DEFAULT_EVENT(getSessionUser(), "Deleted subject " + subjectId));
    }

    @ApiOperation(value = "Deletes the subject with the specified ID or label in the specified project.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully deleted the requested subject."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified subject."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{projectId}/subjects/{subjectId}", method = DELETE)
    public void deleteSubject(final @PathVariable String projectId, final @PathVariable String subjectId) throws Exception {
        // TODO: Dummy implementation: doesn't check subject access
        final XnatSubjectdata subject = retrieveProjectSubject(projectId, subjectId);
        if (subject == null) {
            throw new NotFoundException("Couldn't find subject with ID " + subjectId);
        }
        subject.delete((BaseXnatProjectdata) subject.getProject(projectId, false), getSessionUser(), true, DEFAULT_EVENT(getSessionUser(), "Deleted subject " + subjectId + " in project " + projectId));
    }

    @ApiOperation(value = "Gets the IDs of the experiments accessible by the current user in the specified project.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of IDs of accessible experiments."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/experiments", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveProjectExperiments(final @PathVariable String project) {
        // TODO: Dummy implementation: mostly fine but doesn't check permissions at all, so good with @PreAuthorize on project ID
        return getTemplate().queryForList("SELECT id FROM xnat_experimentdata WHERE project = :project", new MapSqlParameterSource("project", project), String.class);
    }

    @ApiOperation(value = "Gets the experiment with the indicated ID or label associated with the indicated project.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/experiments/{experiment}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatExperimentdata retrieveProjectExperiment(final @PathVariable String project, final @PathVariable String experiment) throws NotFoundException {
        // TODO: Dummy implementation: doesn't include shared experiments, doesn't check permissions, could use @PreFilter for project access, @PostFilter for experiment access.
        try {
            final String resolvedId = getTemplate().queryForObject("SELECT id FROM xnat_experimentdata WHERE project = :project AND id = :experiment OR label = :experiment", new MapSqlParameterSource("project", project).addValue("experiment", experiment), String.class);
            return XnatExperimentdata.getXnatExperimentdatasById(resolvedId, getSessionUser(), false);
        } catch (DataAccessException e) {
            throw new NotFoundException("Couldn't find experiment " + experiment + " in project " + project);
        }
    }

    @ApiOperation(value = "Gets the IDs of the subjects in the specified project.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of subject IDs."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveProjectSubjects(final @PathVariable String project) {
        // TODO: Dummy implementation: doesn't check for project existence or permissions, could use @PreFilter for project access.
        return getTemplate().queryForList("SELECT id FROM xnat_subjectdata WHERE project = :project", new MapSqlParameterSource("project", project), String.class);
    }

    @ApiOperation(value = "Gets the subject with the indicated ID or label in the indicated project.", response = XnatSubjectassessordata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of subject."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatSubjectdata retrieveProjectSubject(final @PathVariable String project, final @PathVariable String subject) throws NotFoundException {
        // TODO: Dummy implementation: doesn't include shared subjects, doesn't check permissions, could use @PreFilter for project/subject access.
        try {
            final String resolvedId = getTemplate().queryForObject("SELECT id FROM xnat_subjectdata WHERE project = :project AND id = :subject OR label = :subject", new MapSqlParameterSource("project", project).addValue("subject", subject), String.class);
            return XnatSubjectdata.getXnatSubjectdatasById(resolvedId, getSessionUser(), false);
        } catch (DataAccessException e) {
            throw new NotFoundException("Couldn't find subject " + subject + " in project " + project);
        }
    }

    @ApiOperation(value = "Gets the list of IDs for experiments associated with the indicated subject in the indicated project.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of experiment IDs."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveProjectSubjectExperiments(final @PathVariable String project, final @PathVariable String subject) {
        // TODO: Dummy implementation: doesn't include shared subjects or experiments, doesn't check permissions, could use @PreFilter for project/subject/experiment access.
        return getTemplate().queryForList("SELECT e.id FROM xnat_experimentdata e LEFT JOIN xnat_subjectassessordata a ON e.id = a.id LEFT JOIN xnat_subjectdata s ON a.subject_id = s.id WHERE :experiment IN (e.id, e.label) AND :subject IN (s.id, s.label) AND s.project = :project", new MapSqlParameterSource("project", project).addValue("subject", subject), String.class);
    }

    @ApiOperation(value = "Gets the experiment with the indicated ID or label associated with the subject with the indicated ID or label in the indicated project.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments/{experiment}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatExperimentdata retrieveProjectSubjectExperiment(final @PathVariable String project, final @PathVariable String subject, final @PathVariable String experiment) throws NotFoundException {
        // TODO: Dummy implementation: doesn't include shared subjects/experiments, doesn't check permissions.
        try {
            final String resolvedId = getTemplate().queryForObject("SELECT e.id FROM xnat_experimentdata e LEFT JOIN xnat_subjectassessordata a ON e.id = a.id LEFT JOIN xnat_subjectdata s ON a.subject_id = s.id WHERE :experiment IN (e.id, e.label) AND :subject IN (s.id, s.label) AND s.project = :project", new MapSqlParameterSource("project", project).addValue("subject", subject).addValue("experiment", experiment), String.class);
            return XnatExperimentdata.getXnatExperimentdatasById(resolvedId, getSessionUser(), false);
        } catch (DataAccessException e) {
            throw new NotFoundException("Couldn't find experiment " + experiment + " for subject " + subject + " in project " + project);
        }
    }

    @ApiOperation(value = "Gets the list of IDs for assessors associated with the indicated experiment and subject in the indicated project.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments/{experiment}/assessors", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveProjectSubjectExperimentAssessors(final @PathVariable String project, final @PathVariable String subject, final @PathVariable String experiment) throws NotFoundException {
        final XnatExperimentdata resolved = retrieveProjectSubjectExperiment(project, subject, experiment);
        return getTemplate().queryForList("SELECT id FROM xnat_imageassessordata WHERE imagesessionid = :experiment", new MapSqlParameterSource("experiment", resolved.getId()), String.class);
    }

    @ApiOperation(value = "Gets the assessor with the indicated ID or label associated with the experiment and subject with the indicated IDs or labels in the indicated project.", response = XnatImageassessordata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments/{experiment}/assessors/{assessor}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatImageassessordata retrieveProjectSubjectExperimentAssessor(final @PathVariable String project, final @PathVariable String subject, final @PathVariable String experiment, final @PathVariable String assessor) throws NotFoundException {
        // TODO: Dummy implementation: doesn't include shared subjects/experiments, doesn't check permissions.
        try {
            final String resolvedId = getTemplate().queryForObject("SELECT a.id FROM xnat_imageassessordata a LEFT JOIN xnat_experimentdata d ON a.id = d.id LEFT JOIN xnat_experimentdata e ON a.imagesession_id = e.id LEFT JOIN xnat_subjectassessordata a ON e.id = a.id LEFT JOIN xnat_subjectdata s ON a.subject_id = s.id WHERE :assessor IN (d.id, d.label) AND :experiment IN (s.id, s.label) AND :subject IN (s.id, s.label) AND s.project = :project", new MapSqlParameterSource("project", project).addValue("subject", subject).addValue("experiment", experiment).addValue("assessor", assessor), String.class);
            return XnatImageassessordata.getXnatImageassessordatasById(resolvedId, getSessionUser(), false);
        } catch (DataAccessException e) {
            throw new NotFoundException("Couldn't find assessor " + assessor + " for experiment " + experiment + " and subject " + subject + " in project " + project);
        }
    }

    @ApiOperation(value = "Gets a list of IDs of the subjects accessible by the current user.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of subject IDs."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subjects", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> retrieveSubjects() {
        // TODO: Dummy implementation: doesn't check permissions.
        return getTemplate().queryForList("SELECT id FROM xnat_subjectdata", EmptySqlParameterSource.INSTANCE, String.class);
    }

    @ApiOperation(value = "Gets the subject with the requested ID.", response = XnatSubjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested subject."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subjects/{subject}", produces = APPLICATION_XML_VALUE, method = GET)
    @ResponseBody
    public XnatSubjectdata retrieveSubject(final @PathVariable String subject) {
        // TODO: Dummy implementation: doesn't check permissions, check with @PreAuthorize on subject ID and corresponding project
        return XnatSubjectdata.getXnatSubjectdatasById(subject, getSessionUser(), false);
    }

    /**
     * Checks that the submitted assessor matches the experiment and assessor IDs.
     *
     * @param experimentId The ID of the experiment with which the assessor is associated.
     * @param assessorId   The ID of the assessor to match.
     * @param assessor     The assessor object.
     *
     * @throws DataFormatException Thrown when either the experiment or assessor ID doesn't match the corresponding properties in the assessor object.
     */
    private void validateExperimentAssessor(@PathVariable final String experimentId, @PathVariable final String assessorId, @RequestBody final XnatImageassessordata assessor) throws DataFormatException {
        if (!StringUtils.equals(assessorId, assessor.getId())) {
            throw new DataFormatException("The assessor ID does not match the ID of the submitted assessor");
        }
        if (!StringUtils.equals(experimentId, assessor.getImagesessionId())) {
            throw new DataFormatException("The experiment ID does not match the ID of the image session ID for the submitted assessor");
        }
    }

    private EventDetails newEventInstance(final String reason, final String comment) {
        return EventUtils.newEventInstance(CATEGORY.DATA, TYPE.WEB_SERVICE, EventUtils.CREATE_VIA_WEB_SERVICE, reason, comment);
    }

    private final NamedParameterJdbcTemplate _template;
    private final SaveItemHelper             _helper;
}
