package org.nrg.xapi.rest.dicom;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.dicom.StudyRoutingModel;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.services.StudyRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api("XNAT Study Routing Management API")
@XapiRestController
@ResponseBody
@RequestMapping("/routing")
@Slf4j
public class StudyRoutingApi extends AbstractXapiProjectRestController {

    @Autowired
    public StudyRoutingApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final StudyRoutingService studyRoutingService) {
        super(userManagementService, roleHolder);
        _studyRoutingService = studyRoutingService;
    }

    @ApiOperation(value = "Gets the Study Routing", notes = "Returns the  Study Routing", response = String.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Study Routing."),
                   @ApiResponse(code = 400, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<StudyRoutingModel> getAllStudyRouting() {
        log.debug("User {} requested all study routings", getSessionUser().getUsername());
        return _studyRoutingService.findAll(getSessionUser());
    }

    @ApiOperation(value = "Gets the Ip Study Routing", notes = "Returns the  Study Routing", response = String.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Study Routing."),
                   @ApiResponse(code = 400, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "{studyInstanceUid}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public StudyRoutingModel getByStudyInstanceId(@ApiParam("The ID of the project to be updated") @PathVariable final String studyInstanceUid) throws InsufficientPrivilegesException, NotFoundException {
        log.debug("User {} requested IpWhitelist", getSessionUser().getUsername());
        return _studyRoutingService.findByStudyInstanceUid(getSessionUser(), studyInstanceUid).orElseThrow(() -> new NotFoundException("Study instance UID routing", studyInstanceUid));
    }

    @ApiOperation(value = "update existing Study Routing", notes = " updating the existing Study Routing", response = void.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Study Routing."),
                   @ApiResponse(code = 400, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "{studyInstanceUid}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = PUT)
    public void updateStudyRouting(@ApiParam("The ID of the studyInstanceUid to be updated") @PathVariable final String studyInstanceUid,
                                   @ApiParam(value = "The ID of the project to be updated.") @RequestParam final String projectId) throws InitializationException, DataFormatException {
        log.debug("User {} requested Study Routing", getSessionUser().getUsername());
        _studyRoutingService.updateStudyRouting(getSessionUser(), studyInstanceUid, projectId);
    }

    @ApiOperation(value = "delete Study Routing", notes = " delete the Study Routing with specified instance Uid", response = void.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested Study Routing."),
                   @ApiResponse(code = 400, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Study Routing wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "{studyInstanceUid}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteStudyRouting(@ApiParam("The ID of the studyInstanceUid to be updated") @PathVariable final String studyInstanceUid) throws InsufficientPrivilegesException, InitializationException {
        log.debug("User {} requested Study Routing", getSessionUser().getUsername());
        _studyRoutingService.deleteStudyRouting(getSessionUser(), studyInstanceUid);
    }

    private final StudyRoutingService _studyRoutingService;
}
