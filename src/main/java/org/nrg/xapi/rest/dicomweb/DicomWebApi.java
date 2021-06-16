/*
 * web: org.nrg.xapi.rest.dicom.AnonymizeApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
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
import org.nrg.framework.exceptions.NrgServiceException;
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
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.nrg.xdat.security.helpers.AccessLevel.Read;

@Api(description = "XNAT DICOM Web API")
@XapiRestController
@RequestMapping(value = "/dicomweb")
public class DicomWebApi extends AbstractXapiProjectRestController {

    private final SearchEngineI _searchEngine;
    private final SiteConfigPreferences _preferences;
    private final PopulatorI _populator;
    private static final Logger _log = LoggerFactory.getLogger("dicomweb");
//    private final ContentNegotiationConfigurer _configurer;


    @Autowired
    public DicomWebApi( final UserManagementServiceI userManagementService,
                        final RoleHolder roleHolder,
                        final SearchEngineI searchEngine,
                        final SiteConfigPreferences preferences,
//                        final ContentNegotiationConfigurer configurer,
                        final PopulatorI populator) {
        super(userManagementService, roleHolder);
        _searchEngine = searchEngine;
        _preferences = preferences;
        _populator = populator;
//        _configurer = configurer;
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
    @XapiRequestMapping(value = "studies", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForStudies( @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;
        UserI user = getUser();
        qidoResponses = _searchEngine.searchForStudies( null, dicomQueryParams, user);

        if (qidoResponses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        qidoResponses.forEach(response -> response.setRetrieveURL(getRetrieveStudyURL(((QIDOResponseStudy) response).getStudyInstanceUID())));
        return new ResponseEntity<>(qidoResponses, HttpStatus.OK);
    }

    @ApiOperation(value = "QIDO-RS SearchForStudies in context of a Session.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 409, message = "Conflict - same study instance uid in multiple projects."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "session/{sessionID}/studies", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForStudiesWithSession( @PathVariable("sessionID") final String sessionID,
                                                                                       @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses;
        UserI user = getUser();
        qidoResponses = _searchEngine.searchForStudies( sessionID, dicomQueryParams, user);

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
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeries( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                           @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException  {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;

        UserI user = getUser();
        qidoResponses = _searchEngine.searchForSeries( null, studyInstanceUID, dicomQueryParams, user);

        if( qidoResponses.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        qidoResponses.forEach( response -> response.setRetrieveURL( getRetrieveStudyURL( studyInstanceUID)));
        return new ResponseEntity<List<? extends QIDOResponse>>( qidoResponses, HttpStatus.OK );
    }

    @ApiOperation(value = "QIDO-RS SearchForSeries with Study Instance UID in the context of a Session.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "session/{sessionID}/studies/{studyInstanceUID}/series", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeriesWithSession( @PathVariable("sessionID") String sessionID,
                                                                                      @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                                      @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException  {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;

        UserI user = getUser();
        qidoResponses = _searchEngine.searchForSeries( sessionID, studyInstanceUID, dicomQueryParams, user);

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
    @XapiRequestMapping(value = "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesUID}/instances",
            produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForInstancesWithSession( @PathVariable("sessionID") String sessionID,
                                                                                      @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                                      @PathVariable("seriesUID") String seriesUID,
                                                                                      @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException  {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;

        UserI user = getUser();
        qidoResponses = _searchEngine.searchForInstances( sessionID, studyInstanceUID, seriesUID, dicomQueryParams, user);

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
    @XapiRequestMapping(value = "series", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeries( @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;
        UserI user = getUser();
        qidoResponses = _searchEngine.searchForStudySeries( null, dicomQueryParams, user);

        if( qidoResponses.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        qidoResponses.forEach( response -> response.setRetrieveURL( getRetrieveSeriesURL( ((QIDOResponseStudySeries)response).getStudyInstanceUID(), ((QIDOResponseStudySeries)response).getSeriesInstanceUID())));
        return new ResponseEntity<List<? extends QIDOResponse>>(qidoResponses, HttpStatus.OK );
    }

    @ApiOperation(value = "QIDO-RS SearchForSeries without Study Instance UID in the context of a Session.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "session/{sessionID}/series", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeriesWithSession( @PathVariable("sessionID") String sessionID, @RequestParam final MultiValueMap<String,String> allRequestParams)
            throws UserNotFoundException, UserInitException, SearchException {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;
        UserI user = getUser();
        qidoResponses = _searchEngine.searchForStudySeries( sessionID, dicomQueryParams, user);

        if( qidoResponses.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        qidoResponses.forEach( response -> response.setRetrieveURL( getRetrieveSeriesURL( ((QIDOResponseStudySeries)response).getStudyInstanceUID(), ((QIDOResponseStudySeries)response).getSeriesInstanceUID())));
        return new ResponseEntity<List<? extends QIDOResponse>>(qidoResponses, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Series Metadata.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve Series metadata."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}/metadata",
            produces = {"application/dicom+json"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveSeriesMetadata( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                        @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                        @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                                        @RequestHeader Map<String, String> headers)
            throws UserNotFoundException, UserInitException, SearchException {
        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<DicomObjectI> instances = new ArrayList<>();
        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveSeries( studyInstanceUID, seriesInstanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Series Metadata with Session ID.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve Series metadata."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/metadata",
            produces = {"application/dicom+json"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveSeriesMetadataWithSession( @PathVariable("sessionID") String sessionID,
                                                                                   @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                                   @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                                   @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                                                   @RequestHeader Map<String, String> headers)
            throws UserNotFoundException, UserInitException, SearchException {
        List<DicomObjectI> instances = new ArrayList<>();
        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveSeries( sessionID, studyInstanceUID, seriesInstanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Instance Metadata with Session ID.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve instance metadata."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{instanceUID}/metadata",
            produces = {"application/dicom+json"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveInstanceMetadataWithSession( @PathVariable("sessionID") String sessionID,
                                                                                     @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                                     @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                                     @PathVariable("instanceUID") String instanceUID,
                                                                                   @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                                                   @RequestHeader Map<String, String> headers)
            throws UserNotFoundException, UserInitException, SearchException {
        List<DicomObjectI> instances = new ArrayList<>();
        UserI user = getUser();
        instances.add( _searchEngine.retrieveInstance( sessionID, studyInstanceUID, seriesInstanceUID, instanceUID, 1, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Instance.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve instance."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}",
            produces = {"multipart/related;type=\"application/dicom\""},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveInstance( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                  @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                  @PathVariable("sopInstanceUID") String sopInstanceUID,
                                                                  @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                                  @RequestHeader MultiValueMap<String, String> headers)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {
        return doRetrieveFrame( studyInstanceUID, seriesInstanceUID, sopInstanceUID, 1, allRequestParams, headers);
    }

    @ApiOperation(value = "WADO-RS Retrieve Frame.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve frame."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}/frames/{frameNumber}",
            produces = {"multipart/related; type=\"application/octet-stream\"",
                    "multipart/related; type=\"image/jpeg\""},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveFrame( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                               @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                               @PathVariable("sopInstanceUID") String sopInstanceUID,
                                                               @PathVariable("frameNumber") int frameNumber,
                                                               @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                               @RequestHeader MultiValueMap<String, String> headers)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {
        List<DicomObjectI> instances = new ArrayList<>();
        UserI user = getUser();
        DicomObjectI instance = _searchEngine.retrieveInstance( null, studyInstanceUID, seriesInstanceUID, sopInstanceUID, frameNumber, user);
        if( instance == null) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        instances.add(instance);
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Frame.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve frame."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "session/{sessionID}/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}/frames/{frameNumber}",
            produces = {"multipart/related; type=\"application/octet-stream\"",
                    "multipart/related; type=\"image/jpeg\""},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveFrameWithSession( @PathVariable("sessionID") String sessionID,
                                                                          @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                               @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                               @PathVariable("sopInstanceUID") String sopInstanceUID,
                                                               @PathVariable("frameNumber") int frameNumber,
                                                               @RequestParam final MultiValueMap<String,String> allRequestParams,
                                                               @RequestHeader MultiValueMap<String, String> headers)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {
        List<DicomObjectI> instances = new ArrayList<>();
        UserI user = getUser();
        DicomObjectI instance = _searchEngine.retrieveInstance( sessionID, studyInstanceUID, seriesInstanceUID, sopInstanceUID, frameNumber, user);
        if( instance == null) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        instances.add(instance);
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Series.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve series."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}", produces = {"multipart/related;type=\"application/dicom\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveSeries(@PathVariable("studyInstanceUID") String studyInstanceUID,
                                                               @PathVariable("seriesInstanceUID") String seriesInstanceUID)
            throws UserNotFoundException, UserInitException, SearchException, NoContentException {

        List<DicomObjectI> instances = new ArrayList<>();
        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveSeries( studyInstanceUID, seriesInstanceUID, user));
        if( instances.isEmpty()) {
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(instances, HttpStatus.OK );
    }

    @ApiOperation(value = "WADO-RS Retrieve Study.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve study."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}", produces = {"multipart/related;type=\"application/dicom\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveStudy(@PathVariable("studyInstanceUID") String studyInstanceUID, HttpServletRequest request) throws UserNotFoundException, UserInitException, SearchException {
        List<DicomObjectI> instances = new ArrayList<>();

        UserI user = getUser();
        instances.addAll( _searchEngine.retrieveStudy( studyInstanceUID, user));
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
    public ResponseEntity<String> doPopulate(@PathVariable("project") String project) throws NrgServiceException, NoContentException {

        UserI user = null;
        try {
            user = getUser();

            _populator.populate( project);

            return new ResponseEntity<>("Success", HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to populate project: project={1}", user, project);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried to populate project: project={1}", user, project);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}