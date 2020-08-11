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
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.nrg.xapi.model.dicomweb.UnsupportedTransferSyntaxException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xapi.rest.dicomweb.populate.PopulatorI;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nrg.xdat.security.helpers.AccessLevel.Read;

@Api(description = "XNAT DICOM Web API")
@XapiRestController
@RequestMapping(value = "/dicomweb")
public class DicomWebApi extends AbstractXapiProjectRestController {

    private final SearchEngineI _searchEngine;
    private final SiteConfigPreferences _preferences;
    private final PopulatorI _populator;
    private static final Logger _log = LoggerFactory.getLogger(DicomWebApi.class);


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
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForStudies( @RequestParam final Map<String,String> allRequestParams) throws NrgServiceException {
        Set<String> paramNames = allRequestParams.keySet();

        if (paramNames == null || paramNames.isEmpty()) {
            // badly formatted query. No query params specified.
        }

        UserI user = null;

        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;
        try {
            user = getUser();
            qidoResponses = _searchEngine.searchForStudies( dicomQueryParams, user);

            if( qidoResponses.isEmpty()) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<? extends QIDOResponse>>(qidoResponses, HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to SearchForStudies.", user);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried QIDO SearchForSeries with params: {1}", user, allRequestParams);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "QIDO-RS SearchForSeries with Study Instance UID.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeries( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                           @RequestParam final Map<String,String> allRequestParams) throws NrgServiceException {
        Set<String> paramNames = allRequestParams.keySet();

        if (paramNames == null || paramNames.isEmpty()) {
            // badly formatted query. No query params specified.
        }

        UserI user = null;

        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;
        try {
            user = getUser();
            qidoResponses = _searchEngine.searchForSeries( studyInstanceUID, dicomQueryParams, user);

            if( qidoResponses.isEmpty()) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<? extends QIDOResponse>>(qidoResponses, HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to SearchForSeries.", user);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried QIDO SearchForSeries with studyInstanceUID={1} and params: {2}", user, studyInstanceUID, allRequestParams);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "QIDO-RS SearchForSeries without Study Instance UID.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed QIDO-RS query."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "series", produces = {"application/dicom+json","multipart/related;type=\"application/dicom+xml\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<? extends QIDOResponse>> doSearchForSeries( @RequestParam final Map<String,String> allRequestParams) throws NrgServiceException {
        Set<String> paramNames = allRequestParams.keySet();

        if (paramNames == null || paramNames.isEmpty()) {
            // badly formatted query. No query params specified.
        }

        UserI user = null;

        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<? extends QIDOResponse> qidoResponses = null;
        try {
            user = getUser();
            qidoResponses = _searchEngine.searchForStudySeries( dicomQueryParams, user);

            if( qidoResponses.isEmpty()) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<? extends QIDOResponse>>(qidoResponses, HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to SearchForSeries.", user);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried QIDO SearchForSeries with params: {1}", user, allRequestParams);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "WADO-RS Retrieve Series Metadata.", response = QIDOResponse.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retieve Series metadata."),
            @ApiResponse(code = 204, message = "No matches."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}/metadata",
            produces = {"application/dicom+json"},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveSeriesMetadata( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                                                  @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                                                  @RequestParam final Map<String,String> allRequestParams,
                                                                                  @RequestHeader MultiValueMap<String, String> headers) throws NrgServiceException {
        UserI user = null;

        QueryParameters dicomQueryParams = new QueryParameters( allRequestParams);
        List<DicomObjectI> instances = new ArrayList<>();
        try {
            user = getUser();
            instances.addAll( _searchEngine.retrieveSeries( studyInstanceUID, seriesInstanceUID, user));
            if( instances.isEmpty()) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(instances, HttpStatus.OK );


//        } catch (IllegalAccessException e) {
//            String msg = MessageFormat.format("Insufficient permission for user {0} to SearchForSeries.", user);
//            _log.warn(msg, e);
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
//            String msg = MessageFormat.format("An error occurred when user {0} tried QIDO SearchForSeries with params: {1}", user, allRequestParams);
//            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
                                                                  @PathVariable("sopInstanceUID") String sopInstanceUID) throws NrgServiceException, NoContentException {
        UserI user = null;
        List<DicomObjectI> instances = new ArrayList<>();
        try {
            user = getUser();
            DicomObjectI instance = _searchEngine.retrieveInstance( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
            if( instance == null) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            instances.add(instance);
            return new ResponseEntity<>(instances, HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to retrieve instance: studyUID={1}, seriesUID={2}, sopInstanceUID={3}", user, studyInstanceUID, seriesInstanceUID, sopInstanceUID);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried to retrieve instance: studyUID={1}, seriesUID={2}, sopInstanceUID={3}", user, studyInstanceUID, seriesInstanceUID, sopInstanceUID);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "WADO-RS Retrieve Frame.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve frame."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}/frames/{frameNumber}",
            produces = {"multipart/related; type=\"application/octet-stream\""},
            method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveFrame( @PathVariable("studyInstanceUID") String studyInstanceUID,
                                                               @PathVariable("seriesInstanceUID") String seriesInstanceUID,
                                                               @PathVariable("sopInstanceUID") String sopInstanceUID,
                                                               @PathVariable("frameNumber") int frameNumber,
                                                               @RequestHeader MultiValueMap<String, String> headers) throws NrgServiceException, NoContentException {
        List<DicomObjectI> instances = new ArrayList<>();
        UserI user = null;
        try {
            user = getUser();
            DicomObjectI instance = _searchEngine.retrieveInstance( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
            if( instance == null) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            instances.add(instance);
            return new ResponseEntity<>(instances, HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to retrieve frame: studyUID={1}, seriesUID={2}, sopInstanceUID={3}, frame={4}", user, studyInstanceUID, seriesInstanceUID, sopInstanceUID, frameNumber);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried to retrieve instance: studyUID={1}, seriesUID={2}, sopInstanceUID={3}, frame={4}", user, studyInstanceUID, seriesInstanceUID, sopInstanceUID, frameNumber);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(UnsupportedTransferSyntaxException.class)
    public ResponseEntity<String> handleUnsupportedTSUIDException(UnsupportedTransferSyntaxException ex) {

        ResponseEntity<String> responseEntity = new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);

        return responseEntity;
    }

    @ExceptionHandler(TransCoderException.class)
    public ResponseEntity<String> handleTransCoderException(TransCoderException ex) {

        ResponseEntity<String> responseEntity = new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        return responseEntity;
    }


    @ApiOperation(value = "WADO-RS Retrieve Series.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve series."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}/series/{seriesInstanceUID}", produces = {"multipart/related;type=\"application/dicom\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveSeries(@PathVariable("studyInstanceUID") String studyInstanceUID,
                                                               @PathVariable("seriesInstanceUID") String seriesInstanceUID) throws NrgServiceException, NoContentException {

        UserI user = null;
        List<DicomObjectI> instances = new ArrayList<>();
        try {
            user = getUser();
            instances.addAll( _searchEngine.retrieveSeries( studyInstanceUID, seriesInstanceUID, user));
            if( instances.isEmpty()) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(instances, HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to retrieve series: studyUID={1}, seriesUID={2}", user, studyInstanceUID, seriesInstanceUID);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried to retrieve series: studyUID={1}, seriesUID={2}", user, studyInstanceUID, seriesInstanceUID);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @ApiOperation(value = "WADO-RS Retrieve Study.", response = DicomObjectI.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully performed WADO-RS retrieve study."),
            @ApiResponse(code = 403, message = "Insufficient permissions to perform the request."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "studies/{studyInstanceUID}", produces = {"multipart/related;type=\"application/dicom\""}, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<DicomObjectI>> doRetrieveStudy(@PathVariable("studyInstanceUID") String studyInstanceUID) throws NrgServiceException, NoContentException {

        UserI user = null;
        List<DicomObjectI> instances = new ArrayList<>();
        try {
            user = getUser();
            instances.addAll( _searchEngine.retrieveStudy( studyInstanceUID, user));
            if( instances.isEmpty()) {
                return new ResponseEntity<>( HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(instances, HttpStatus.OK );

        } catch (IllegalAccessException e) {
            String msg = MessageFormat.format("Insufficient permission for user {0} to retrieve study: studyUID={1}", user, studyInstanceUID);
            _log.warn(msg, e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            String msg = MessageFormat.format("An error occurred when user {0} tried to retrieve study: studyUID={1}", user, studyInstanceUID);
            _log.error(msg, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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