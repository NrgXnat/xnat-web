/*
 * web: org.nrg.xapi.rest.dicom.DICOMwebAPI
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2021, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.dicomweb;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NoContentException;
import org.nrg.xapi.model.dicomweb.*;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xapi.rest.dicomweb.populate.PopulatorI;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xapi.rest.dicomweb.search.SearchException;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.nrg.xdat.security.helpers.AccessLevel.Read;

@Api(description = "XNAT DICOM Web API")
@XapiRestController
@RequestMapping(value = "/dicomweb")
public class DicomWebApi extends AbstractXapiProjectRestController {

    private final SearchEngineI _searchEngine;
    private final SiteConfigPreferences _preferences;
    private final PopulatorI _populator;
    private static final Logger _log = LoggerFactory.getLogger("dicomweb");


    @Autowired
    public DicomWebApi( final UserManagementServiceI userManagementService,
                        final RoleHolder roleHolder,
                        final SearchEngineI searchEngine,
                        final SiteConfigPreferences preferences,
                        final PopulatorI populator) {
        super(userManagementService, roleHolder);
        _searchEngine = searchEngine;
        _preferences = preferences;
        _populator = populator;
    }

    /**
     * Get the authenticated user or the guest user.
     *
     * @return the authenticated user or the guest user.
     * @throws UserNotFoundException
     * @throws UserInitException
     * @throws IllegalAccessError
     */
    public UserI getUser() throws UserNotFoundException, UserInitException, IllegalAccessError {
        UserI user = super.getSessionUser();
        if( user == null) {
            if( ! _preferences.getRequireLogin()) {
                user = getUserManagementService().getGuestUser();
            }
            else {
                throw new IllegalAccessError();
            }
        }
        return user;
    }

    @ApiOperation(value = "QIDO-RS SearchForStudies.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 409, message = "Conflict - same study instance uid in multiple projects."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {"studies", "session/{sessionID}/studies"},
            produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForStudies( @RequestParam final Optional<String> sessionID,
                                                                            @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        UserI user = getUser();
        List<? extends QIDOResponse> qidoResponses = _searchEngine.searchForStudies( sessionID.orElse(null), dicomQueryParams, user);

        if (qidoResponses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        qidoResponses.forEach(response -> response.setRetrieveURL(getRetrieveStudyURL(((QIDOResponseStudy) response).getStudyInstanceUID())));
        return new ResponseEntity<>(qidoResponses, HttpStatus.OK);
    }

   @ApiOperation(value = "QIDO-RS SearchForSeries with Study Instance UID.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {"studies/{studyInstanceUID}/series", "session/{sessionID}/studies/{studyInstanceUID}/series"},
            produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeries( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                           @PathVariable("sessionID") Optional<String> sessionID,
                                                                           @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException  {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        UserI user = getUser();
        List<? extends QIDOResponse> qidoResponses = _searchEngine.searchForSeries( sessionID.orElse(null), studyInstanceUID, dicomQueryParams, user);

        if( qidoResponses.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        qidoResponses.forEach( response -> response.setRetrieveURL( getRetrieveStudyURL( studyInstanceUID)));
        return new ResponseEntity<List<? extends QIDOResponse>>( qidoResponses, HttpStatus.OK );
    }

    @ApiOperation(value = "QIDO-RS SearchForInstances.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {"studies/{studyInstanceUID}/series/{seriesUID}/instances",
            "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesUID}/instances"},
            produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForInstances( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                              @PathVariable("seriesUID") String seriesUID,
                                                                              @PathVariable("sessionID") Optional<String> sessionID,
                                                                              @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException  {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        UserI user = getUser();
        List<? extends QIDOResponse> qidoResponses = _searchEngine.searchForInstances( sessionID.orElse(null), studyInstanceUID, seriesUID, dicomQueryParams, user);

        if( qidoResponses.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        qidoResponses.forEach( response -> response.setRetrieveURL( getRetrieveStudyURL( studyInstanceUID)));
        return new ResponseEntity<List<? extends QIDOResponse>>( qidoResponses, HttpStatus.OK );
    }

    private String getRetrieveStudyURL( String studyInstanceUID) {
        return String.format( "%s/dicomweb/studies/%s", _preferences.getSiteUrl(), studyInstanceUID);
    }

    private String getRetrieveSeriesURL(String studyInstanceUID, String seriesInstanceUID) {
        return String.format( "%s/dicomweb/studies/%s/series/%s", _preferences.getSiteUrl(), studyInstanceUID, seriesInstanceUID);
    }

    @ApiOperation(value = "QIDO-RS SearchForSeries without Study Instance UID.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {"series", "session/{sessionID}/series"},
            produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeries( @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                                           @PathVariable Optional<String> sessionID)
            throws UserNotFoundException, UserInitException, SearchException {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        UserI user = getUser();
        List<? extends QIDOResponse> qidoResponses = _searchEngine.searchForStudySeries( sessionID.orElse(null), dicomQueryParams, user);

        if( qidoResponses.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        qidoResponses.forEach( response -> response.setRetrieveURL( getRetrieveSeriesURL( ((QIDOResponseStudySeries)response).getStudyInstanceUID(), ((QIDOResponseStudySeries)response).getSeriesInstanceUID())));
        return new ResponseEntity<List<? extends QIDOResponse>>(qidoResponses, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Study Metadata.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve Study metadata."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {"studies/{studyInstanceUID}/metadata", "sessions/{sessionID}/studies/{studyInstanceUID}/metadata"},
            produces = {"application/dicom+json"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObject>> doRetrieveStudyMetadata(@PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                     @PathVariable Optional<String> sessionID)
            throws UserNotFoundException, UserInitException, SearchException {
        List<DicomObject> instances = new ArrayList<>();
        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveStudy( sessionID.orElse(null), studyInstanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Series Metadata.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve Series metadata."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {"studies/{studyInstanceUID}/series/{seriesInstanceUID}/metadata","session/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/metadata"},
            produces = {"application/dicom+json"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObject>> doRetrieveSeriesMetadata(@PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                      @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                      @PathVariable("sessionID") Optional<String> sessionID)
            throws UserNotFoundException, UserInitException, SearchException {
        List<DicomObject> instances = new ArrayList<>();
        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveSeries( sessionID.orElse(null), studyInstanceUID, seriesInstanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Instance Metadata.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve instance metadata."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {"studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{instanceUID}/metadata",
            "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{instanceUID}/metadata"},
            produces = {"application/dicom+json"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObject>> doRetrieveInstanceMetadata(@PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                        @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                        @PathVariable("instanceUID") String instanceUID,
                                                                        @PathVariable("sessionID") Optional<String> sessionID)
            throws UserNotFoundException, UserInitException, SearchException {
        List<DicomObject> instances = new ArrayList<>();
        UserI user = getUser();
        instances.add( _searchEngine.retrieveInstance( sessionID.orElse(null), studyInstanceUID, seriesInstanceUID, instanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Instance.", response = DicomObject.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve instance."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {                    "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}",
                                 "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}"},
            produces = { "application/dicom",
                    "application/dicom+json",
                    "application/dicom+xml"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<DicomObject> doRetrieveInstanceSinglePart(@PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                    @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                    @PathVariable("sopInstanceUID") String sopInstanceUID,
                                                                    @PathVariable("sessionID") Optional<String> sessionID)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {
        UserI user = getUser();
        DicomObject instance = _searchEngine.retrieveInstance( sessionID.orElse(null), studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
        if( instance == null) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>( instance, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Instance With Session ID.", response = DicomObject.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve instance."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {                   "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}",
                               "sessions/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}"},
            produces = {"multipart/related;type=\"application/dicom\"",
                    "multipart/related;type=\"application/dicom+json\"",
                    "multipart/related;type=\"application/dicom+xml\"",
                    "multipart/related;type=\"application/octet-stream\""},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObject>> doRetrieveInstancesMultiPart(@PathVariable("sessionID") Optional<String> sessionID,
                                                                @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                @PathVariable("sopInstanceUID") String sopInstanceUID)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {
        List<DicomObject> instances = new ArrayList<>();
        UserI user = getUser();
        DicomObject instance = _searchEngine.retrieveInstance( sessionID.orElse(null), studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
        if( instance == null) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        instances.add(instance);
        return new ResponseEntity<>( instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Frame.", response = DicomObject.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve frame."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}/frames/{frameNumbers}",
            produces = {"multipart/related; type=\"application/octet-stream\"",
                        "multipart/related; type=\"image/jpeg\""},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomFrame>> doRetrieveFramesList( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                              @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                              @PathVariable("sopInstanceUID") String sopInstanceUID,
                                                              @PathVariable("frameNumbers") String frameNumbers)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {
        UserI user = getUser();
        List<Integer> frameList = Arrays.stream(frameNumbers.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        List<DicomFrame> frames = _searchEngine.retrieveFrames( null, studyInstanceUID, seriesInstanceUID, sopInstanceUID, frameList, user);
        if( frames == null || frames.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        else if( frames.size() < frameList.size()) {
            return new ResponseEntity<>( frames, HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(frames, HttpStatus.OK );
        }
    }

    @ApiOperation(value = "WADO-RS Retrieve Frame with session ID.", response = DicomObject.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve frame."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {                    "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}/frames/{frameNumber}",
                                 "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}/frames/{frameNumber}"},
            produces = {"multipart/related; type=\"application/octet-stream\"",
                        "multipart/related; type=\"image/jpeg\""},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObject>> doRetrieveFrames(@PathVariable("sessionID") Optional<String> sessionID,
                                                              @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                              @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                              @PathVariable("sopInstanceUID") String sopInstanceUID,
                                                              @PathVariable("frameNumber") int frameNumber,
                                                              @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                              @RequestHeader MultiValueMap<String, String> headers)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {
        List<DicomObject> instances = new ArrayList<>();
        UserI user = getUser();
        DicomObject instance = _searchEngine.retrieveInstance( sessionID.orElse(null), studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
        if( instance == null) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        instances.add(instance);
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Series.", response = DicomObject.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve series."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {                     "studies/{studyInstanceUID}/series/{seriesInstanceUID}",
                                 "sessions/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}"},
            produces = {"multipart/related;type=\"application/dicom\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObject>> doRetrieveSeries(@PathVariable("sessionID") Optional<String> sessionID,
                                                              @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                              @PathVariable("seriesInstanceUID") String seriesInstanceUID)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {

        List<DicomObject> instances = new ArrayList<>();
        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveSeries( sessionID.orElse(null), studyInstanceUID, seriesInstanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Study.", response = DicomObject.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve study."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = {                    "studies/{studyInstanceUID}",
                                 "session/{sessionID}/studies/{studyInstanceUID}"},
            produces = {"multipart/related;type=\"application/dicom\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObject>> doRetrieveStudy( @PathVariable("sessionID") Optional<String> sessionID,
                                                              @PathVariable("studyInstanceUID") String studyInstanceUID)
            throws UserNotFoundException, UserInitException, SearchException {
        List<DicomObject> instances = new ArrayList<>();

        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveStudy( sessionID.orElse(null), studyInstanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "Populate DB for pre-existing project.", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed populate."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "populate/{project}", produces = {"application/text"}, method = RequestMethod.PUT, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<String> doPopulate(@PathVariable("project") String project) throws Exception {

        UserI user = getUser();
        _populator.populate( project);

        return new ResponseEntity<>("Success populating project: " + project, HttpStatus.OK );
    }

}