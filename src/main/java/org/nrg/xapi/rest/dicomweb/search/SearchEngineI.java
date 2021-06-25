package org.nrg.xapi.rest.dicomweb.search;

import org.nrg.xapi.model.dicomweb.DicomFrame;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.QIDOResponse;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xft.security.UserI;

import java.io.IOException;
import java.util.List;

public interface SearchEngineI {

    List<DicomObjectI> getStudy(String studyInstanceUID) throws IOException;

    DicomObjectI[] getStudyAsArray(String studyInstanceUID) throws IOException;

    List<? extends QIDOResponse> searchForStudies( String sessionID, QueryParameters queryParameters, UserI user) throws SearchException;

    List<? extends QIDOResponse> searchForSeries( String sessionID, String studyInstanceUID, QueryParameters queryParameters, UserI user) throws SearchException;

    List<? extends QIDOResponse> searchForStudySeries( String sessionID, QueryParameters queryParameters, UserI user) throws SearchException;

    List<? extends QIDOResponse> searchForInstances( String sessionID, String studyInstanceUID, String seriesInstanceUID, QueryParameters queryParameters, UserI user) throws SearchException;

    DicomObjectI retrieveInstance( String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws SearchException;

    DicomFrame retrieveFrame(String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, int frameNumber, UserI user) throws SearchException;

    List<DicomObjectI> retrieveSeries( String sessionID, String studyInstanceUID, String seriesInstanceUID, UserI user) throws SearchException;

    List<DicomObjectI> retrieveStudy( String sessionID, String studyInstanceUID, UserI user) throws SearchException;
}
