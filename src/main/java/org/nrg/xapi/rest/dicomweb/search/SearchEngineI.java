package org.nrg.xapi.rest.dicomweb.search;

import org.nrg.xapi.model.dicomweb.DicomFrame;
import org.nrg.xapi.model.dicomweb.DicomFrames;
import org.nrg.xapi.model.dicomweb.DicomObject;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xft.security.UserI;

import java.io.IOException;
import java.util.List;

public interface SearchEngineI {

    List<DicomObject> getStudy(String studyInstanceUID) throws IOException;

    DicomObject[] getStudyAsArray(String studyInstanceUID) throws IOException;

    List<? extends QIDOResponse> searchForStudies( String projectID, String sessionID, QueryParameters queryParameters, UserI user) throws SearchException;

    List<? extends QIDOResponse> searchForSeries( String projectID, String sessionID, String studyInstanceUID, QueryParameters queryParameters, UserI user) throws SearchException;

    List<? extends QIDOResponse> searchForStudySeries( String projectID, String sessionID, QueryParameters queryParameters, UserI user) throws SearchException;

    List<? extends QIDOResponse> searchForInstances( String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, QueryParameters queryParameters, UserI user) throws SearchException;

    DicomObject retrieveInstance( String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws SearchException;

    DicomFrame retrieveFrame(String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, int frameNumber, UserI user) throws SearchException;

    DicomFrames retrieveFrames(String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, List<Integer> frameNumbers, UserI user) throws SearchException;

    List<DicomObject> retrieveSeries( String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, UserI user) throws SearchException;

    List<DicomObject> retrieveStudy( String projectID, String sessionID, String studyInstanceUID, UserI user) throws SearchException;
}
