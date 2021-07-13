//package org.nrg.xapi.resources;
//
//import static org.springframework.web.bind.annotation.RequestMethod.GET;
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.nrg.framework.annotations.XapiRestController;
//import org.nrg.xapi.exceptions.DataFormatException;
//import org.nrg.xapi.exceptions.InitializationException;
//import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
//import org.nrg.xapi.exceptions.NoContentException;
//import org.nrg.xapi.exceptions.NotAuthenticatedException;
//import org.nrg.xapi.exceptions.NotFoundException;
//import org.nrg.xapi.rest.AbstractXapiProjectRestController;
//import org.nrg.xapi.rest.XapiRequestMapping;
//import org.nrg.xdat.om.XnatProjectdata;
//import org.nrg.xdat.security.services.RoleHolder;
//import org.nrg.xdat.security.services.UserManagementServiceI;
//import org.nrg.xft.security.UserI;
//import org.nrg.xnat.dto.resource.DIRResourceDto;
//import org.nrg.xnat.dto.resource.MediaTypeUtil;
//import org.nrg.xnat.services.resources.DIRResourceService;
//import org.nrg.xnat.services.resources.impl.DIRResourceServiceImpl.InvalidFileCharacters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//import lombok.extern.slf4j.Slf4j;
//
//@Api("XNAT DIR or XAR Resource Management API")
//@XapiRestController
//@ResponseBody
//@Slf4j
//public class DIRResourceApi extends AbstractXapiProjectRestController {
//	
//	@Autowired
//    public DIRResourceApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final DIRResourceService dIRResourceService) {
//        super(userManagementService, roleHolder);
//        _dIRResourceService = dIRResourceService;
//    }
//
//	
//	 @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
//	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
//	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
//	                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
//	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
//	    @XapiRequestMapping(value = {"/experiments/{experimentId}/DIR","/projects/{projectId}/experiments/{experimentId}/DIR"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = GET)
//	    public List<DIRResourceDto>  getAllDIRResources(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
//	    		@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
//	    		@ApiParam(value = "The value  of the filepath.") @RequestParam(required = false) final String filepath,
//	    		@ApiParam(value = "The value of the recursive.") @RequestParam(required = false) final boolean recursive,
//	    		@ApiParam(value = "The value of the isXarReference.") @RequestParam(required = false) final boolean isXarReference) throws NotFoundException, DataFormatException, NotAuthenticatedException, InvalidFileCharacters {
//			
//		 log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
//	    	 return _dIRResourceService.findAllDIRResources(getSessionUser(), projectId, experimentId, filepath, recursive, isXarReference);
//	    }
//	 
//	 
//	@ApiOperation(value = "Downloads the contents of the specified resource XAR.", response = StreamingResponseBody.class)
//	@ApiResponses({ @ApiResponse(code = 200, message = "The requested resources were successfully downloaded."),
//			@ApiResponse(code = 204, message = "No resources were specified."),
//			@ApiResponse(code = 400, message = "Something is wrong with the request format."),
//			@ApiResponse(code = 403, message = "The user is not authorized to access one or more of the specified resources."),
//			@ApiResponse(code = 404, message = "The request was valid but one or more of the specified resources was not found."),
//			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
//	@XapiRequestMapping(value = {"/experiments/{experimentId}/XAR","/projects/{projectId}/experiments/{experimentId}/XAR"}, produces = MediaTypeUtil.APPLICATION_XAR, method = RequestMethod.GET)
//	@ResponseBody
//public ResponseEntity<StreamingResponseBody> downloadXarResourceZip(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
//		@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
//		@ApiParam(value = "The value  of the filepath.") @RequestParam(required = false) final String filepath,
//		@ApiParam(value = "The value  of the recursive.") @RequestParam(required = false) final boolean recursive,
//		@ApiParam(value = "The value  of the isXarReference.") @RequestParam(required = false) final boolean isXarReference,
//		@ApiParam(value = "The value  of the compression.") @RequestParam(required = false) final String compression,
//		@ApiParam(value = "The value  of the sRequest.") final  HttpServletRequest sRequest,
//		@ApiParam(value = "The value  of the hRequest.") @RequestHeader HttpHeaders hRequest) throws InsufficientPrivilegesException, NoContentException, NotFoundException, NotAuthenticatedException, InvalidFileCharacters, InitializationException {
//		final UserI user = getSessionUser();
//		
//		StreamingResponseBody result  = _dIRResourceService.findAllXARResources(user, projectId, experimentId, filepath, recursive, isXarReference,sRequest,hRequest,compression );
//		
//		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaTypeUtil.APPLICATION_XAR)
//				.header(HttpHeaders.CONTENT_DISPOSITION, _dIRResourceService.getContentDisposition())
//				.body(result);
//	}
//
//	 private final  DIRResourceService _dIRResourceService;
//}
