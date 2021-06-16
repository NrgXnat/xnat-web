package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpServletRequest;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.dto.resource.MediaTypeUtil;
import org.nrg.xnat.services.resources.DIRResourceService;
import org.nrg.xnat.services.resources.impl.DIRResourceServiceImpl.InvalidFileCharacters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT DIR Resource Management API")
@XapiRestController
@ResponseBody
@RequestMapping("/projects")
@Slf4j
public class DIRResourceApi extends AbstractXapiProjectRestController {
	
	@Autowired
    public DIRResourceApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final DIRResourceService dIRResourceService) {
        super(userManagementService, roleHolder);
        _dIRResourceService = dIRResourceService;
    }

	
	 @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = {"/experiments/{experimentId}/DIR","/experiments/{experimentId}/XAR"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypeUtil.APPLICATION_XAR}, method = GET)
	    public void  getAllDIRResources(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
	    		@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
	    		@ApiParam(value = "The ID of the project.") @RequestParam(required = false) final String filepath,
	    		@ApiParam(value = "The ID of the project.") @RequestParam(required = false) final boolean recursive,
	    		@ApiParam(value = "The ID of the project.") @RequestParam(required = false) final boolean isXarReference,
	    		@ApiParam(value = "The ID of the project.") @RequestParam(required = false) final String compression,
	    		@ApiParam(value = "The ID of the project.") final  HttpServletRequest sRequest,
	    		@ApiParam(value = "The ID of the project.") @RequestHeader HttpHeaders request) throws NotFoundException, DataFormatException, NotAuthenticatedException, InvalidFileCharacters {
			
		 log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
	    	 _dIRResourceService.findAllDIRResources(getSessionUser(), projectId, experimentId, filepath, recursive, isXarReference, request, sRequest, compression);
	    }
	 
	 private final  DIRResourceService _dIRResourceService;
}
