//package org.nrg.xapi.extensions;
//
//import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
//import static org.springframework.web.bind.annotation.RequestMethod.GET;
//import static org.springframework.web.bind.annotation.RequestMethod.POST;
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.nrg.framework.annotations.XapiRestController;
//import org.nrg.xapi.exceptions.DataFormatException;
//import org.nrg.xapi.exceptions.InitializationException;
//import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
//import org.nrg.xapi.exceptions.NotFoundException;
//import org.nrg.xapi.rest.AbstractXapiProjectRestController;
//import org.nrg.xapi.rest.XapiRequestMapping;
//import org.nrg.xdat.security.services.RoleHolder;
//import org.nrg.xdat.security.services.UserManagementServiceI;
//import org.nrg.xnat.extensions.util.TriageUtil;
//import org.nrg.xnat.services.extensions.TriageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//import lombok.extern.slf4j.Slf4j;
//
//@Api("XNAT Triage Management API")
//@XapiRestController
//@ResponseBody
//@RequestMapping("/services/triage/projects")
//@Slf4j
//public class TriageApi extends AbstractXapiProjectRestController {
//	
//	@Autowired
//    public TriageApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final TriageService triageService) {
//        super(userManagementService, roleHolder);
//        _triageService = triageService;
//    }
//	
//	@ApiOperation(value = "Gets the All Triage resource", notes = "Returns the  Triage resource", response = String.class, responseContainer = "single")
//    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
//    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
//                   @ApiResponse(code = 404, message = "The requested Triage resource wasn't found."),
//                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
//    @XapiRequestMapping(value = "{projectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
//    public List<TriageUtil> getAll(@ApiParam("The ID of the project ") @PathVariable final String projectId,
//    		@ApiParam("The value of Http Servlet request") HttpServletRequest request) throws NotFoundException, DataFormatException, InsufficientPrivilegesException, InitializationException {
//		log.debug("User {} requested Triage resource", getSessionUser().getUsername());
//		return _triageService.findTriageByProjectId(getSessionUser(),projectId, request);
//	}
//	
//	@ApiOperation(value = "create Triage resource", notes = " creating the Triage resource", response = void.class, responseContainer = "single")
//    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested  Triage resource."),
//    	           @ApiResponse(code = 400, message = "The requested  Triage resource wasn't found."),
//                   @ApiResponse(code = 404, message = "The requested  Triage resource wasn't found."),
//                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
//	 @XapiRequestMapping(value = {"/{PROJECT}/resources","/{PROJECT}/resources/{XNAME}","/{PROJECT}/resources/{XNAME}/files","/{PROJECT}/resources/{XNAME}/files/{FILE}"}, 
//	 					consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
//	 					produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
//    public void createTriage(@ApiParam("The ID of the project ") @PathVariable final String projectId,
//    		@ApiParam("The value of xname") @PathVariable(required = false) final String xname,
//    		@ApiParam("The value of file") @PathVariable(required = false) final String file,
//    		@ApiParam("The value of eventReason") @RequestParam(required = false) final String eventReason,
//    		@ApiParam("The value of eventComment") @RequestParam(required = false) final String eventComment,
//    		@ApiParam("The value of eventId") @RequestParam(required = false) final String eventId,
//    		@ApiParam("The value of target") @RequestParam(required = false) final String target,
//    		@ApiParam("The value of inbody") @RequestParam(required = false) final boolean inbody,
//    		@ApiParam("The value of overwrite") @RequestParam(required = false) final String overwrite,
//    		@ApiParam("The value of format") @RequestParam(required = false) final String format,
//    		@ApiParam("The value of content") @RequestParam(required = false) final String content,
//    		@ApiParam("The value of event_reason") @RequestParam(required = false) final String event_reason,
//    		@ApiParam("The value of extract") @RequestParam(required = false) final String extract,
//    		@ApiParam("The value of Http Servlet request") HttpServletRequest request) throws InitializationException, DataFormatException {
//		log.debug("User {} requested Study Routing", getSessionUser().getUsername());
//		_triageService.create(getSessionUser(), projectId, xname, file, eventReason, eventComment, eventId, target, inbody, overwrite, format, content, event_reason, extract, request);
//	}
//	
//	
//	@ApiOperation(value = "delete  Triage resource", notes = " delete the  Triage resource ", response = void.class, responseContainer = "single")
//    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested  Triage resource."),
//    	           @ApiResponse(code = 400, message = "The requested  Triage resource wasn't found."),
//                   @ApiResponse(code = 404, message = "The requested  Triage resource wasn't found."),
//                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
//	 @XapiRequestMapping(value = {"/{PROJECT}/resources","/{PROJECT}/resources/{XNAME}","/{PROJECT}/resources/{XNAME}/files","/{PROJECT}/resources/{XNAME}/files/{FILE}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
//    public void deleteStudyRouting(@ApiParam("The ID of the project ") @PathVariable final String projectId,
//    		@ApiParam("The value of xname") @PathVariable(required = false) final String xname,
//    		@ApiParam("The value of file") @PathVariable(required = false) final String file,
//    		@ApiParam("The value of xname") @RequestParam(required = false) final String eventReason,
//    		@ApiParam("The value of xname") @RequestParam(required = false) final String eventComment,
//    		@ApiParam("The value of xname") @RequestParam(required = false) final String eventId) throws InsufficientPrivilegesException, InitializationException {
//		log.debug("User {} requested Study Routing", getSessionUser().getUsername());
//		_triageService.deleteTriage(getSessionUser(), projectId, xname, file, eventReason, eventComment, eventId);
//	}
//	
//	private final TriageService _triageService;
//
//}
