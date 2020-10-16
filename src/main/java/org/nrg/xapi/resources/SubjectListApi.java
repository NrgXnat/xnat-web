package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.subjects.XnatSubject;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.SubjectListService;
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
public class SubjectListApi extends AbstractXapiProjectRestController {
	 
	@Autowired
	public SubjectListApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final SubjectListService subjectListService) {
		super(userManagementService, roleHolder);
		_subjectListService = subjectListService;
	}
	
	@ApiOperation(value = "Gets the requested subject", notes= "Returns the subject with the specified ID", response = XnatSubject.class, responseContainer = "List")
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested subject."),
         @ApiResponse(code = 404, message = "The requested subject wasn't found."),
         @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	@XapiRequestMapping(value = "/subjects/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<XnatSubject> getSubjectById(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId) throws Exception {
		log.debug("Controller Api- get Resources by subjectId");
		XnatSubject subjectData = _subjectListService.findById(getSessionUser(),subjectId);
	    if (subjectData == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(subjectData, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get list of subjects", notes= "The subjects function returns a list of all subjects configured in the XNAT system.", response = XnatSubject.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured subjects."),
        @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	@XapiRequestMapping(value = "/subjects" , produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatSubject>> getSubjectList(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId) throws Exception {
		log.debug("Controller Api- get Resources by subjectId");
		List<XnatSubject> subjectData = _subjectListService.getAll(getSessionUser());
	    if (subjectData == null) {
			throw new NotFoundException("No Project with ID was found.");
		}
		return new ResponseEntity<>(subjectData, HttpStatus.OK);
	}
	
	private final SubjectListService _subjectListService;
}
