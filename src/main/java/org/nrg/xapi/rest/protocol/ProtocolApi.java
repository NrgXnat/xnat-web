package org.nrg.xapi.rest.protocol;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.model.XnatDatatypeprotocolI;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.protocol.ProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT protocol Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ProtocolApi extends AbstractXapiProjectRestController {
    @Autowired
    public ProtocolApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ProtocolService protocolService) {
        super(userManagementService, roleHolder);
        _protocolService = protocolService;
    }

	
	 @ApiOperation(value = "Gets the requested  protocol", notes = "Returns the  protocol with the specified ID", response = XnatProjectdataI.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested protocol wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = "/projects/{projectId}/protocols/{protocolId}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	    public XnatDatatypeprotocolI getByProjectIdAndProtocolId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
																 @ApiParam(value = "The ID of the protocol.") @PathVariable final String protocolId,
																 @ApiParam(value = "The datatype of value.") @RequestParam(name = "dataType") final String dataType,
																 @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
																 @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
																 @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
																 @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
																 @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws NotFoundException, DataFormatException {
	    	log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
	    	return _protocolService.findByProjectIdAndProtocolId(getSessionUser(), projectId, protocolId, dataType, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }
	 
	 
	 
	 @ApiOperation(value = "update the requested  protocol", notes = "Returns the  protocol with the specified ID", response = XnatProjectdataI.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested protocol wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = "/projects/{projectId}/protocols/{protocolId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
							produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method =PUT)
	    public XnatDatatypeprotocolI update(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
	    		@ApiParam("The protocol to be updated.") @RequestBody final XnatDatatypeprotocolI protocol,
	    		@ApiParam(value = "The ID of the protocol.") @PathVariable final String protocolId,
	    		@ApiParam(value = "The datatype of value.") @RequestParam(name = "dataType") final String dataType,
	    		@ApiParam(value = "The gender of value.") @RequestParam(name = "gender", required = false) final String gender,
	    		@ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
				@ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
				@ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
				@ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
				@ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws NotFoundException, DataFormatException, InitializationException {
	    	log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
	    	return _protocolService.update(getSessionUser(), projectId, protocolId, dataType, gender, protocol, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }
	 
	 
	 @ApiOperation(value = "delete the requested  protocol", notes = "delete the  protocol with the specified ID", response = XnatProjectdataI.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested protocol wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = "/projects/{projectId}/protocols/{protocolId}", produces = MediaType.APPLICATION_JSON_VALUE, method = DELETE)
	    public void delete(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
	    		@ApiParam(value = "The ID of the protocol.") @PathVariable final String protocolId,
	    		@ApiParam(value = "The datatype of value.") @RequestParam(name = "dataType") final String dataType,
	    		@ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
				@ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
				@ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
				@ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
				@ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws NotFoundException, DataFormatException, InitializationException {
	    	log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
	    	 _protocolService.delete(getSessionUser(), projectId, protocolId,dataType, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }
	 
	 private final ProtocolService _protocolService;
}
