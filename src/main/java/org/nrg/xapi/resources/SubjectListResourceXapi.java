package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ProjectSubjectListService;
import org.nrg.xnat.services.resources.SubjectListResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT subject Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class SubjectListResourceXapi extends AbstractXapiProjectRestController {
	private static final Logger _log = LoggerFactory.getLogger(SubjectListResourceXapi.class);
	 
	@Autowired
	public SubjectListResourceXapi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final SubjectListResourceService subjectListResourceService) {
		super(userManagementService, roleHolder);
		_subjectListResourceService = subjectListResourceService;
	}
	
	@ApiOperation(value = "Get the resources for a project, subject, or experiment", notes = "The resource should be identified by standard item-relative paths, such as /subjects or /subjects/XNAT_S0001", response = XnatAbstractresourceI.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "The resource(s) were successfully retrieved."),
			@ApiResponse(code = 403, message = "The user doesn't have permission to access the requested item(s)"),
			@ApiResponse(code = 403, message = "The the requested item(s) don't exist"),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = {
			"/subjects",
			"/subjects/{subjectId}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<String> getResources(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)
					
			throws Exception {
		_log.debug("Controller Api- get Resources by subjectId");
		final String subjectData = _subjectListResourceService.getSubjectResource(getSessionUser(),subjectId);
	    if (subjectData == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(subjectData, HttpStatus.OK);
	}
	
	private final SubjectListResourceService _subjectListResourceService;
}
