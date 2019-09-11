package org.nrg.xapi.rest.data;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/*
experiments
experiments/{experiment}
experiments/{experiment}/assessors
experiments/{experiment}/assessors/{assessor}
projects
projects/{project}
projects/{project}/experiments
projects/{project}/experiments/{experiment}
projects/{project}/subjects
projects/{project}/subjects/{subject}
projects/{project}/subjects/{subject}/experiments
projects/{project}/subjects/{subject}/experiments/{experiment}
projects/{project}/subjects/{subject}/experiments/{experiment}/assessors
projects/{project}/subjects/{subject}/experiments/{experiment}/assessors/{assessor}
subjects
subjects/{subject}
 */

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
    }

    @ApiOperation(value = "Gets a list of experiments accessible to the current user.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the list of experiments."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> getExperiments() {
        // TODO: Dummy implementation
        return Arrays.asList("experiment1", "experiment2");
    }

    @ApiOperation(value = "Gets the experiment with the specified ID.", response = XnatExperimentdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested experiment."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified experiment."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experiment}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public XnatExperimentdata getExperiment(final @PathVariable String experiment) {
        // TODO: Dummy implementation.
        return XnatExperimentdata.getXnatExperimentdatasById(experiment, getSessionUser(), false);
    }

    @ApiOperation(value = "Gets the assessors associated with the experiment with the specified ID.", response = String.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experiment}/assessors", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public List<String> getExperimentAssessors(final @PathVariable String experiment) {
        return getTemplate().queryForList("SELECT id FROM xnat_imageassessordata WHERE imagesession_id = :experiment", new MapSqlParameterSource("experiment", experiment), String.class);
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "experiments/{experiment}/assessors/{assessor}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getExperimentAssessors(final @PathVariable String experiment, final @PathVariable String assessor) throws NotFoundException {
        try {
            final String resolvedId = getTemplate().queryForObject("SELECT id FROM xnat_imageassessordata a LEFT JOIN xnat_experimentdata x ON a.id = x.id WHERE a.imagesession_id = :experiment AND x.id = :assessor OR x.label = :assessor", new MapSqlParameterSource("experiment", experiment).addValue("assessor", assessor), String.class);
            return XnatExperimentdata.getXnatExperimentdatasById(resolvedId, getSessionUser(), false);
        } catch (DataAccessException e) {
            throw new NotFoundException("Couldn't find experiment " + experiment + " and assessor " + assessor);
        }
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/experiments", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/experiments/{experiment}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments/{experiment}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments/{experiment}/assessors", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "projects/{project}/subjects/{subject}/experiments/{experiment}/assessors/{assessor}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subjects", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    @ApiOperation(value = "Gets the object identified by the REST path.", response = Object.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the requested object."),
                   @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
                   @ApiResponse(code = 403, message = "The object doesn't exist or the current user is not authorized to perform the requested operation on the specified object."),
                   @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "subjects/{subject}", produces = APPLICATION_JSON_VALUE, method = GET)
    @ResponseBody
    public Object getDataObject() {
        return new Object();
    }

    private final NamedParameterJdbcTemplate _template;
}
