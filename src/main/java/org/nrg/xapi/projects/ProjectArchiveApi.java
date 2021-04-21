package org.nrg.xapi.projects;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.projects.ProjectArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT project Archive Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProjectArchiveApi extends AbstractXapiProjectRestController {
    @Autowired
    public ProjectArchiveApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProjectArchiveService projectArchiveService) {
        super(userManagementService, roleHolder);
        _projectArchiveService = projectArchiveService;
    }
    
    @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = ArcProject.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    			   @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = {"/projects/{projectId}/archive_spec","/config/{projectId}/archive_spec"}, produces = MediaType.APPLICATION_XML_VALUE, method = GET)
    public ArcProject getProjectById(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException  {
        log.debug("Controller Api- get project by ID {}", projectId);
        return _projectArchiveService.findByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(ArcProject.SCHEMA_ELEMENT_NAME, projectId));
    }
    private final ProjectArchiveService _projectArchiveService;
}
