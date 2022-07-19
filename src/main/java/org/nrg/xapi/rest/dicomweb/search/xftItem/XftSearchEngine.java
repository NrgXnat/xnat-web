package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xapi.model.dicomweb.*;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xapi.rest.dicomweb.mediator.Conflict;
import org.nrg.xapi.rest.dicomweb.mediator.Mediator;
import org.nrg.xapi.rest.dicomweb.search.InstanceFilter;
import org.nrg.xapi.rest.dicomweb.search.SearchEngineI;
import org.nrg.xapi.rest.dicomweb.search.SearchException;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatDcmcatalogBean;
import org.nrg.xdat.bean.CatDcmentryBean;
import org.nrg.xdat.model.CatDcmentryI;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.ItemI;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.utils.CatalogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class XftSearchEngine implements SearchEngineI {

    private final NamedParameterJdbcTemplate _jdbcTemplate;
    private final DateTimeService _dateTimeService;
    private final QueryParamToCriteriaService _queryParamService;
    private final CatalogService _catalogService;
    private final DicomObjectFactory _dicomObjectFactory;
    private final Mediator _mediator;
    private static final Logger _log = LoggerFactory.getLogger("dicomweb");
    private final InstanceFilter _instanceFilter;

    @Autowired
    public XftSearchEngine(final UserManagementServiceI userManagementService,
                           final CatalogService catalogService,
                           NamedParameterJdbcTemplate jdbcTemplate,
                           final DicomObjectFactory dicomObjectFactory,
                           final Mediator mediator) {
        this._dateTimeService = new DateTimeService();
        this._queryParamService = new QueryParamToCriteriaService( _dateTimeService);
        this._jdbcTemplate = jdbcTemplate;
        this._catalogService = catalogService;
        this._mediator = mediator;
        this._instanceFilter = new InstanceFilter();
        this._dicomObjectFactory = dicomObjectFactory;
    }

    @Override
    public List<DicomObject> getStudy(String studyInstanceUID) throws IOException {
        return null;
    }

    @Override
    public DicomObject[] getStudyAsArray(String studyInstanceUID) throws IOException {
        return new DicomObject[0];
    }

    @Override
    public List<? extends QIDOResponse> searchForStudies( String projectID, String sessionID, QueryParameters queryParameters, UserI user) throws SearchException {
        CriteriaCollection cc = _queryParamService.mapStudy( projectID, sessionID, queryParameters);

        ItemCollection ic;
        try {
            if (cc.size() == 0) {
                ic = ItemSearch.GetAllItems("xnat:imageSessionData", user, false);
            } else {
                ic = ItemSearch.GetItems("xnat:imageSessionData", cc, user, false);
            }
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }

        List<XnatImagesessiondata> sessions =  ic.getItems().stream().map( XnatImagesessiondata::new).collect(Collectors.toList());
        Optional<List<Conflict>> conflicts = _mediator.getConflicts( sessions);

        if( conflicts.isPresent()) {
            throw new SearchException( SearchException.Type.STUDY_INSTANCE_UID_CONFLICT, "This query matches study-instance UID in multiple projects.");
        }

        QIDOStudyResponseList responses = new QIDOStudyResponseList();
//        List<QIDOResponse> responses = new ArrayList();

        for (ItemI item : ic.getItems()) {
            XnatImagesessiondata session = new XnatImagesessiondata(item);
            QIDOResponseStudy response = _dicomObjectFactory.createQIDOResponseStudy();
            response.setStudyDate(session.getExperimentdata().getDate());
            response.setStudyTime(session.getExperimentdata().getTime());
            response.setAccessionNumber(session.getDcmaccessionnumber());
            response.setInstanceAvailability("ONLINE");
            response.setStudyInstanceUID(session.getUid());
            response.setPatientID(session.getDcmpatientid());
//            response.setPatientsName( session.getSubjectData().getLabel());
            response.setPatientsName(session.getDcmpatientname());
            response.setModalitiesInStudy(getModalitiesInStudy(session));
            response.setPatientsSex(session.getSubjectData().getGender());
            response.setPatientsBirthDate(session.getSubjectData().getDOB());
            response.setStudyID(session.getStudyId());
            response.setNumberOfStudyRelatedSeries(countSeries(session));
            response.setNumberOfStudyRelatedInstances(countStudyInstances(session));
            response.setReferringPhysiciansName("");
            responses.add(response);
        }

        // ItemSearch is not doing combined date-time search. ItemSearch searches by study date.
        // Do combined date-time matching here by filtering responses.
        // Dicom instances with missing studyDate or studyTime do not match.
        if (queryParameters.hasStudyTime() && queryParameters.hasStudyDate()) {
            ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
            String studyDateRangeString = queryParameters.getStudyDateRange();
            String studyTimeRangeString = queryParameters.getStudyTimeRange();
            Interval queryInterval = _dateTimeService.parseDicomDateAndTimeRangeStrings(studyDateRangeString, studyTimeRangeString, zoneOffset);
            responses = responses.stream()
                    .filter(response -> {
                        String responseDateString = response.getStudyDate();
                        String responseTimeString = response.getStudyTime();
                        if( responseTimeString == null || responseDateString == null) {
                            return false;
                        }
                        else {
                            Interval responseInterval = _dateTimeService.parseDicomDateAndTimeRangeStrings(responseDateString, responseTimeString, zoneOffset);
                            return _dateTimeService.hasOverlap(responseInterval, queryInterval);
                        }
                    })
                    .collect(Collectors.toCollection(QIDOStudyResponseList::new));
        }

        int from = Math.min(responses.size(), queryParameters.getOffset());
        int to = Math.min(responses.size(), from + queryParameters.getLimit());
        return responses.subList(from, to);
    }

    @Override
    public List<? extends QIDOResponse> searchForSeries( String projectID, String sessionID, String studyInstanceUID, QueryParameters queryParameters, UserI user) throws SearchException {
        CriteriaCollection cc = _queryParamService.mapSeries( projectID, sessionID, studyInstanceUID, queryParameters);

        ItemCollection ic;
        try {
            if( cc.size() == 0) {
                ic = ItemSearch.GetAllItems( "xnat:imageScanData", user, false);
            }
            else {
                ic = ItemSearch.GetItems( "xnat:imageScanData", cc, user, false);
            }
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }

        // TODO
        // Fix this; there needs to be something before this that catches the fact that the user has no access to this session.
        try {
            getSession(studyInstanceUID, user);
        } catch (Exception e) {
            // This user does not have access to this session. So, clear the list of scans.
            // The loop below will have an empty input to process.
            ic.clear();
        }

        List<QIDOResponse> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagescandata scandata = new XnatImagescandata(item);
            QIDOResponseSeries response = _dicomObjectFactory.createQIDOResponseSeries();
            response.setModality( scandata.getModality());
            response.setSeriesDescription( scandata.getSeriesDescription());
            response.setSeriesInstanceUID( scandata.getUid());
            response.setSeriesNumber( (scandata.getSeriesNumber() != null)? scandata.getSeriesNumber().toString(): "");
            response.setPerformedProcedureStepStartDate( scandata.getStartDate());
            scandata.getStarttime();
            response.setPerformedProcedureStepStartTime( scandata.getStarttime());
//            response.setNumberOfSeriesRelatedInstances( countSeriesInstances( scandata));
            try {
                response.setNumberOfSeriesRelatedInstances( countSeriesInstances( getSession( studyInstanceUID, user).getArchiveRootPath(), scandata));
            }
            catch( Exception e) {
                throw new SearchException( SearchException.Type.UNEXPECTED, e);
            }

            responses.add( response);
        }
        int from = Math.min( responses.size(), queryParameters.getOffset());
        int to = Math.min( responses.size(), from + queryParameters.getLimit());
        return responses.subList( from, to);
    }

    @Override
    public List<? extends QIDOResponse> searchForStudySeries( String projectID, String sessionID, QueryParameters queryParameters, UserI user) throws SearchException {
        List<? extends QIDOResponse> responses = null;

        if( queryParameters.hasStudyLevel()) {
            List<? extends QIDOResponse> studyResponses = searchForStudySeriesByStudyParams( projectID, sessionID, queryParameters, user);

            if( queryParameters.hasSeriesLevel()) {
                Iterator<? extends QIDOResponse> it = studyResponses.iterator();
                while( it.hasNext()) {
                    QIDOResponseStudySeries responseStudySeries = (QIDOResponseStudySeries) it.next();
                    String studyInstanceUID = responseStudySeries.getStudyInstanceUID();

                    responses = searchForStudySeriesByStudyUID( projectID, sessionID, studyInstanceUID, queryParameters, user);
                }
            }
            else {
                responses = studyResponses;
            }
        }
        else if( queryParameters.hasSeriesLevel()) {
            responses = searchForStudySeriesBySeries( projectID, sessionID, queryParameters, user);
        }
        else {
            responses = new ArrayList<>();
        }
        int from = Math.min( responses.size(), queryParameters.getOffset());
        int to = Math.min( responses.size(), from + queryParameters.getLimit());
        return responses.subList( from, to);
    }

    @Override
    public List<? extends QIDOResponse> searchForInstances( String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, QueryParameters queryParameters, UserI user) throws SearchException {
        CriteriaCollection cc = _queryParamService.mapInstances( projectID, sessionID, studyInstanceUID, seriesInstanceUID, queryParameters);

        ItemCollection ic;
        try {
            ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }

        List<QIDOResponse> responses = new ArrayList();
        if( ic.getItems().size() == 1) {
            XnatImagesessiondata sessionData = new XnatImagesessiondata( ic.get(0));
            String localSessionID = (sessionID == null) ? sessionData.getId() : sessionID;
            XnatImagescandata scan = sessionData.getScans_scan().stream()
                    .filter( XnatImagescandata.class::isInstance)
                    .map( XnatImagescandata.class::cast)
                    .filter( scn -> scn.getUid().equals( seriesInstanceUID))
                    .findAny()
                    .orElseThrow( () -> {
                            String msg = String.format("Failed to find scan. sessionID=%s, studyUID=%s, seriesUID=%s", sessionID, studyInstanceUID, seriesInstanceUID);
                            return new SearchException( SearchException.Type.UNEXPECTED, msg);
                    });

            XnatResourcecatalog catalog;
            try {
                catalog = _catalogService.getDicomResourceCatalog(localSessionID, scan.getId());
                final Path dicomRootPath = Paths.get(catalog.getUri()).getParent();
                final CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreate(dicomRootPath.toString(), catalog, null);
                final CatDcmcatalogBean dcmCatalog = (CatDcmcatalogBean) catalogData.catBean;
                List<CatEntryI> catEntryList = dcmCatalog.getEntries_entry();
                catEntryList.stream()
                        .filter(CatDcmentryBean.class::isInstance)
                        .map(CatDcmentryBean.class::cast)
                        .filter( entry -> _instanceFilter.match( entry, queryParameters))
                        .forEach(entry -> {
                            QIDOResponseInstance response = _dicomObjectFactory.createQIDOResponseInstance();
                            response.setInstanceNumber(entry.getInstancenumber().toString());
                            response.setSopInstanceUID(entry.getUid());
                            responses.add(response);
                        });
            } catch (ClientException | ServerException e) {
                throw new SearchException(SearchException.Type.UNEXPECTED, "Error finding scan catalog.", e);
            }
        }

        int from = Math.min( responses.size(), queryParameters.getOffset());
        int to = Math.min( responses.size(), from + queryParameters.getLimit());
        return responses.subList( from, to);
    }

    @Override
    public DicomObject retrieveInstance( String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws SearchException {
        try {
            DicomObject instance = null;
            XnatImagesessiondata session = getSession( projectID, sessionID, studyInstanceUID, user);
            if (session != null) {
                XnatImagescandata scan = getScan(studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
                if (scan != null) {
                    instance = getInstance(session.getArchiveRootPath(), scan, sopInstanceUID);
                }
            }
            return instance;
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }
    }

    @Override
    public DicomFrame retrieveFrame( String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, int frame, UserI user) throws SearchException {
        try {
            XnatImagesessiondata session = getSession( sessionID, studyInstanceUID, user);
            XnatImagescandata scan = getScan( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
            DicomImageObject instance = getInstance( session.getArchiveRootPath(), scan, sopInstanceUID);
            return (instance != null)? new DicomFrame( instance, frame): null;
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }
    }

    @Override
    public DicomFrames retrieveFrames( String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, List<Integer> frameNumbers, UserI user) throws SearchException {
        try {
            XnatImagesessiondata session = getSession( projectID, sessionID, studyInstanceUID, user);
            XnatImagescandata scan = getScan( studyInstanceUID, seriesInstanceUID, sopInstanceUID, user);
            DicomImageObject instance = getInstance( session.getArchiveRootPath(), scan, sopInstanceUID);
            if( instance != null) {
                // TODO: Fix for multiple frames
//                int ix = frameNumbers.get(0);
//                DicomFrame f1 = new DicomFrame(instance, ix);
                return frameNumbers.stream().map( fn -> new DicomFrame( instance, fn)).collect(Collectors.toCollection(DicomFrames::new));
            }
            else {
                return new DicomFrames();
            }
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }
    }

    @Override
    public List<DicomObject> retrieveSeries( String projectID, String sessionID, String studyInstanceUID, String seriesInstanceUID, UserI user) throws SearchException {
        try {
            XnatImagesessiondata session = getSession( projectID, sessionID, studyInstanceUID, user);
            XnatImagescandata scan = getScan( studyInstanceUID, seriesInstanceUID, null, user);
            List<DicomObject> instances = new ArrayList<>();
            if (scan != null) {
                instances = getInstances(scan);
            }
            return instances;
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }
    }

    @Override
    public List<DicomObject> retrieveStudy( String projectID, String sessionID, String studyInstanceUID, UserI user) throws SearchException {

        List<DicomObject> instances = new ArrayList<>();
        XnatImagesessiondata session = getSession( projectID, sessionID, studyInstanceUID, user);
        for( XnatImagescandataI scan: session.getScans_scan()) {
//            instances.addAll( getInstances( session.getArchiveRootPath(), scan));
            instances.addAll( getInstances( scan));
        }

        return instances;
    }

    public List<? extends QIDOResponse> searchForStudySeriesByStudy( QueryParameters queryParameters, UserI user) throws SearchException {
        CriteriaCollection cc = _queryParamService.mapStudy( queryParameters);

        return searchForStudySeriesByStudy( cc, user);
    }

    public List<? extends QIDOResponse> searchForStudySeriesBySeries( String projectID, String sessionID, QueryParameters queryParameters, UserI user) throws SearchException {
        CriteriaCollection cc = _queryParamService.mapSeries( projectID, sessionID, queryParameters);

        return searchForStudySeriesByStudy( cc, user);
    }

    public List<? extends QIDOResponse> searchForStudySeriesByStudyUID( String projectID, String sessionID, String studyInstanceUID, QueryParameters queryParameters, UserI user) throws SearchException {
        CriteriaCollection cc ;
        if( studyInstanceUID != null) cc = _queryParamService.mapSeries( projectID, sessionID, studyInstanceUID, queryParameters);
        else  cc = _queryParamService.mapSeries( projectID, sessionID, queryParameters);

        return searchForStudySeriesByStudy( cc, user);
    }

    private List<? extends QIDOResponse> searchForStudySeriesByStudy( CriteriaCollection cc, UserI user) throws SearchException {
        ItemCollection ic;
        try {
            ic = ItemSearch.GetItems( "xnat:imageScanData", cc, user, false);
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }

        _log.debug("Found {} items.", ic.size());

//        QIDOStudyResponseList responses = new QIDOStudyResponseList();
        List<QIDOResponseStudySeries> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagescandata scandata = new XnatImagescandata(item);
            _log.debug("Item: {}", scandata);
            _log.debug("Scandata's ImageSessionData: {}", scandata.getImageSessionData());

            // TODO Fix this
            // Need a better check on security for this study/series object
            boolean skip = false;
            try {
                scandata.getImageSessionData();
            } catch (Exception e) {
                String yy = "";
                skip = true;
//                continue;
            }
            if (! skip) {
                QIDOResponseStudySeries response = _dicomObjectFactory.createQIDOResponseStudySeries();
                response.setModality(scandata.getModality());
                response.setSeriesDescription(scandata.getSeriesDescription());
                response.setSeriesInstanceUID(scandata.getUid());
                response.setSeriesNumber((scandata.getSeriesNumber() != null) ? scandata.getSeriesNumber().toString() : "");
                response.setPerformedProcedureStepStartDate(scandata.getStartDate());
                response.setPerformedProcedureStepStartTime(scandata.getStarttime());
                XnatImagesessiondata x = null;
                //String y = null;
                try {
/*
                    x = scandata.getImageSessionData();
                    if (x == null) {
                        String z = "xxx";
                    } else {
                        y = x.getArchiveRootPath();
                    }
 */

                    String archiveRootPath = scandata.getImageSessionData().getArchiveRootPath();
                    response.setNumberOfSeriesRelatedInstances(countSeriesInstances(archiveRootPath, scandata));
                } catch (Exception e) {
                    skip = true;
//                    throw new SearchException(SearchException.Type.UNEXPECTED, e);
                }

                if (! skip) {
                    response.setStudyDate(scandata.getImageSessionData().getExperimentdata().getDate());
                    response.setStudyTime(scandata.getImageSessionData().getExperimentdata().getTime());
                    response.setAccessionNumber(scandata.getImageSessionData().getDcmaccessionnumber());
                    response.setInstanceAvailability("ONLINE");
                    response.setStudyInstanceUID(scandata.getImageSessionData().getUid());
                    response.setPatientID(scandata.getImageSessionData().getDcmpatientid());
                    response.setPatientsName(scandata.getImageSessionData().getDcmpatientname());
                    response.setModalitiesInStudy(getModalitiesInStudy(scandata.getImageSessionData()));
                    response.setPatientsSex(scandata.getImageSessionData().getSubjectData().getGender());
                    response.setPatientsBirthDate(scandata.getImageSessionData().getSubjectData().getDOB());
                    response.setStudyID(scandata.getImageSessionData().getStudyId());
                    response.setNumberOfStudyRelatedSeries(countSeries(scandata.getImageSessionData()));
                    response.setNumberOfStudyRelatedInstances(countStudyInstances(scandata.getImageSessionData()));
                    response.setReferringPhysiciansName("");

                    responses.add(response);
                }
            }
        }
        return responses;
    }

    private List<? extends QIDOResponse> searchForStudySeriesByStudyParams( String projectID, String sessionID, QueryParameters queryParameters, UserI user) throws SearchException {
        CriteriaCollection cc = _queryParamService.mapStudy( projectID, sessionID, queryParameters);
        ItemCollection ic;
        try {
            ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);
        }
        catch( Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, e);
        }


//        QIDOStudyResponseList responses = new QIDOStudyResponseList();
        List<QIDOResponseStudySeries> responses = new ArrayList();
        for( ItemI item: ic.getItems()) {
            XnatImagesessiondata session = new XnatImagesessiondata(item);

            List<XnatImagescandataI> scans = session.getScans_scan();
            for( XnatImagescandataI scan: scans) {
                QIDOResponseStudySeries response = _dicomObjectFactory.createQIDOResponseStudySeries();
                response.setModality(scan.getModality());
                response.setSeriesDescription(scan.getSeriesDescription());
                response.setSeriesInstanceUID(scan.getUid());
                response.setSeriesNumber((scan.getSeriesNumber() != null)? scan.getSeriesNumber().toString(): "");
                response.setPerformedProcedureStepStartDate(scan.getStartDate());
                response.setPerformedProcedureStepStartTime(scan.getStarttime());
                try {
                    response.setNumberOfSeriesRelatedInstances( countSeriesInstances( session.getArchiveRootPath(), scan));
                }
                catch( Exception e) {
                    throw new SearchException( SearchException.Type.UNEXPECTED, e);
                }

            response.setStudyDate(session.getExperimentdata().getDate());
                response.setStudyTime(session.getExperimentdata().getTime());
                response.setAccessionNumber(session.getDcmaccessionnumber());
                response.setInstanceAvailability("ONLINE");
                response.setStudyInstanceUID(session.getUid());
                response.setPatientID(session.getDcmpatientid());
                response.setPatientsName(session.getDcmpatientname());
                response.setModalitiesInStudy(getModalitiesInStudy(session));
                response.setPatientsSex(session.getSubjectData().getGender());
                response.setPatientsBirthDate(session.getSubjectData().getDOBDisplay());
                response.setStudyID(session.getStudyId());
                response.setNumberOfStudyRelatedSeries( countSeries( session));
                try {
                    response.setNumberOfStudyRelatedInstances( countStudyInstances( session));
                }
                catch( Exception e) {
                    throw new SearchException( SearchException.Type.UNEXPECTED, e);
                }

                responses.add(response);
            }
        }
        return responses;
    }

    private String getModalitiesInStudy( XnatImagesessiondata session) {
        SortedSet<String> modalitySet = new TreeSet<>();
        for( XnatImagescandataI scan: session.getScans_scan()) {
            modalitySet.add( scan.getModality());
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = modalitySet.iterator();
        while( it.hasNext()) {
            sb.append( it.next());
            if( it.hasNext()) sb.append("\\");
        }
        return sb.toString();
    }

    private int countSeries1( UserI user, XnatImagesessiondata session) {
        ArrayList<XnatImagescandata> imagescandata = XnatImagescandata.getXnatImagescandatasByField("xnat:imagescandata/image_session_id", session.getId(), user, false);
        return imagescandata.size();
    }
    private int countSeries( XnatImagesessiondata session) {
        List<XnatImagescandataI> scans = session.getScans_scan();
        return scans.size();
    }

    /**
     * Returns the sum of instances in each scan in the provided session in which the user has access.
     *
     * @param user
     * @param session
     * @return number of instances in the session to which the user has access.
     * @throws Exception if error reading scan catalog.
     */
    private int countStudyInstances1( UserI user, XnatImagesessiondata session) throws SearchException {
        ArrayList<XnatImagescandata> imagescandata = XnatImagescandata.getXnatImagescandatasByField("xnat:imagescandata/image_session_id", session.getId(), user, false);

        int count = 0;
        for( XnatImagescandata scan: imagescandata) {
            Integer n = scan.getInstanceCount();
            if( n == null) {
                try {
                    n = getInstanceCount( session.getArchiveRootPath(), scan );
                }
                catch( Exception e) {
                    throw new SearchException( SearchException.Type.UNEXPECTED, e);
                }

            }
            count += n;
        }
        return count;
    }

    private int countStudyInstances( XnatImagesessiondata session) throws SearchException {
        List<XnatImagescandataI> scans = session.getScans_scan();

        int count = 0;
        for( XnatImagescandataI scan: scans) {
            Integer n = scan.getInstanceCount();
            if( n == null) {
                try {
                    n = getInstanceCount( session.getArchiveRootPath(), scan );
                }
                catch( Exception e) {
                    throw new SearchException( SearchException.Type.UNEXPECTED, e);
                }

            }
            count += n;
        }
        return count;
    }

    private int countSeriesInstances( XnatImagescandata scandata) {
        int count = 0;
//        count = (scandata.getInstanceCount() != null)? scandata.getInstanceCount(): querySeriesInstanceCount( scandata);
        count = scandata.getFrames();
        return count;
    }

    private int countSeriesInstances( String archiveRootPath, XnatImagescandataI imageScanData) {
        int count = 0;
        for( XnatAbstractresourceI resourceI: imageScanData.getFile()) {
            if( XnatResourcecatalog.class.isInstance( resourceI)) {
                XnatResourcecatalog catResource = (XnatResourcecatalog) resourceI;
                if( ("RAW".equals( catResource.getContent()) || "secondary".equals( catResource.getContent())) && "DICOM".equals( catResource.getFormat())) {
                    CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource, null);
                    File catalogFile = CatalogUtils.getCatalogFile( archiveRootPath, catResource);
                    String scanRootPath = catalogFile.getParentFile().getAbsolutePath();
                    if( CatDcmcatalogBean.class.isInstance( catalog1)) {
                        CatDcmcatalogBean dcmcatalog = (CatDcmcatalogBean) catalog1;
                        for( CatEntryI entry: CatalogUtils.getEntriesByFilter(dcmcatalog, new CatalogUtils.CatEntryFilterI() {
                            @Override
                            public boolean accept(CatEntryI entry) {
                                return true;
                            }
                        })) {
                            CatDcmentryI dcmentry = (CatDcmentryI) entry;
                            if( dcmentry != null) {
                                count++;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

//    private int querySeriesInstanceCount( XnatImagescandata scandata) {
//        int count = 0;
//        count = _jdbcTemplate.queryForObject(QUERY_GET_INSTANCE_COUNT_IN_SERIES, new MapSqlParameterSource("scandataId", scandata.getId()), Integer.class);
//        return count;
//    }

    private CriteriaCollection parseDateCriteria( String dateString) {
        return parseRangeCriteria( "xnat:experimentData/date", dateString);
    }

    private CriteriaCollection parseTimeCriteria( String timeString) {
        return parseRangeCriteria( "xnat:experimentData/time", timeString);
    }

    private CriteriaCollection parseRangeCriteria( String xmlPath, String value) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        if( value.contains("-")) {
            if( value.startsWith("-")) {
                cc.addClause( xmlPath, "<=" , value);
            }
            else if( value.endsWith("-")) {
                cc.addClause( xmlPath, ">=" , value);
            }
            else {
                String[] dates = value.split("-");
                cc.addClause( xmlPath, ">=" , dates[0]);
                cc.addClause( xmlPath, "<=" , dates[1]);
            }
        }
        else {
            cc.addClause( xmlPath, "=" , value);
        }
        return cc;
    }

    private CriteriaCollection parsePatientNameCriteria( String pName) {
        CriteriaCollection cc = new CriteriaCollection( "AND");
        if( pName.contains("*") || pName.contains("?")) {
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "LIKE", pName.replaceAll("[\\*\\?]", "%"));
        }
        else {
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "=", pName);
        }
        return cc;
    }

    private XnatImagesessiondata getSession(String studyInstanceUID, UserI user) throws SearchException {
        try {
            CriteriaCollection cc = new CriteriaCollection("AND");
            cc.addClause("xnat:imageSessionData/uid", "=", studyInstanceUID);

            ItemCollection ic = ItemSearch.GetItems("xnat:imageSessionData", cc, user, false);

            XnatImagesessiondata session = new XnatImagesessiondata(ic.getFirst());
            return session;
        }
        catch (Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, "Error getting session data for studyInstanceUID: " + studyInstanceUID, e);
        }
    }

    private XnatImagesessiondata getSession( String sessionID, String studyInstanceUID, UserI user) throws SearchException {
        try {
            CriteriaCollection cc = new CriteriaCollection("AND");
            if( sessionID != null) {
                cc.addClause("xnat:imageSessionData/id", "=", sessionID);
            }
            cc.addClause("xnat:imageSessionData/uid", "=", studyInstanceUID);

            ItemCollection ic = ItemSearch.GetItems("xnat:imageSessionData", cc, user, false);

            XnatImagesessiondata session = new XnatImagesessiondata(ic.getFirst());
            return session;
        }
        catch (Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, "Error getting session data for studyInstanceUID: " + studyInstanceUID, e);
        }
    }

    private XnatImagesessiondata getSession( String projectID, String sessionID, String studyInstanceUID, UserI user) throws SearchException {
        try {
            CriteriaCollection cc = new CriteriaCollection("AND");
            if( projectID != null) {
                cc.addClause("xnat:experimentdata", "=", projectID);
            }
            if( sessionID != null) {
                cc.addClause("xnat:imageSessionData/id", "=", sessionID);
            }
            cc.addClause("xnat:imageSessionData/uid", "=", studyInstanceUID);

            ItemCollection ic = ItemSearch.GetItems("xnat:imageSessionData", cc, user, false);

            XnatImagesessiondata session = new XnatImagesessiondata(ic.getFirst());
            return session;
        }
        catch (Exception e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, "Error getting session data for studyInstanceUID: " + studyInstanceUID, e);
        }
    }

    private XnatImagescandata getScan( String studyInstanceUID, String seriesInstanceUID, String sopInstanceUID, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");
        cc.addClause( "xnat:imageSessionData/uid", "=" , studyInstanceUID);
        cc.addClause( "xnat:imageScanData/uid", "=" , seriesInstanceUID);

        ItemCollection ic = ItemSearch.GetItems( "xnat:imageScanData", cc, user, false);

        XnatImagescandata scan = null;
        if( ic.size() > 0) {
            scan = new XnatImagescandata( ic.get(0));
        }
        if( ic.size() > 1) {
            _log.warn("Multiple scans (" + ic.size() + ") with study uid: " + studyInstanceUID + ", series uid: " + seriesInstanceUID);
        }
        return scan;
    }

    private DicomImageObject getInstance(String archiveRootPath, XnatImagescandata imageScanData, String sopInstanceUID) throws IOException {
        File file = null;
        for(XnatAbstractresourceI resourceI: imageScanData.getFile()) {
            if( XnatResourcecatalog.class.isInstance( resourceI)) {
                XnatResourcecatalog catResource = (XnatResourcecatalog) resourceI;
                if( ("RAW".equals( catResource.getContent()) || "secondary".equals( catResource.getContent())) && "DICOM".equals( catResource.getFormat())) {
                    CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource, null);
                    File catalogFile = CatalogUtils.getCatalogFile( archiveRootPath, catResource);
                    String scanRootPath = catalogFile.getParentFile().getAbsolutePath();
                    if( CatDcmcatalogBean.class.isInstance( catalog1)) {
                        CatDcmcatalogBean dcmcatalog = (CatDcmcatalogBean) catalog1;
                        CatDcmentryI dcmEntry = CatalogUtils.getDCMEntryByUID( dcmcatalog, sopInstanceUID);
                        if( dcmEntry != null) {
                            file = CatalogUtils.getFile( dcmEntry, scanRootPath, null);
                            break;
                        }
                    }
                }
            }
        }
        return (file == null)? null: _dicomObjectFactory.createDicomObject( file, false);
    }

    private DicomFrame getFrame( String archiveRootPath, XnatImagescandata imageScanData, String sopInstanceUID, int frameNumber) throws IOException {
        DicomImageObject dobj = getInstance( archiveRootPath, imageScanData, sopInstanceUID);

        return ( dobj != null)? new DicomFrame( dobj, frameNumber): null;
    }

//    private List<DicomObjectI> getInstances( String archiveRootPath, XnatImagescandataI imageScanData) throws IOException {
//        List<DicomObjectI> instances = new ArrayList<>();
//        for( XnatAbstractresourceI resourceI: imageScanData.getFile()) {
//            if( XnatResourcecatalog.class.isInstance( resourceI)) {
//                XnatResourcecatalog catResource = (XnatResourcecatalog) resourceI;
//                if( ("RAW".equals( catResource.getContent()) || "secondary".equals( catResource.getContent())) && "DICOM".equals( catResource.getFormat())) {
//                    CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource, null);
//                    File catalogFile = CatalogUtils.getCatalogFile( archiveRootPath, catResource);
//                    String scanRootPath = catalogFile.getParentFile().getAbsolutePath();
//                    if( CatDcmcatalogBean.class.isInstance( catalog1)) {
//                        CatDcmcatalogBean dcmcatalog = (CatDcmcatalogBean) catalog1;
//                        for( CatEntryI entry: CatalogUtils.getEntriesByFilter(dcmcatalog, new CatalogUtils.CatEntryFilterI() {
//                            @Override
//                            public boolean accept(CatEntryI entry) {
//                                return true;
//                            }
//                        })) {
//                            CatDcmentryI dcmentry = (CatDcmentryI) entry;
//                            if( dcmentry != null) {
//                                File file = CatalogUtils.getFile( dcmentry, scanRootPath, null);
//                                if( file != null) {
//                                    instances.add( DicomObjectFactory.create(file));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return instances;
//    }

    private List<DicomObject> getInstances(XnatImagescandataI scandata) throws SearchException {
        try {
            XnatResourcecatalog dicomResourceCatalog = _catalogService.getDicomResourceCatalog(scandata.getImageSessionId(), scandata.getId());
            // TODO There are two bad assumptions in this method
            // 1. It assumes that all scans have at least one DICOM file. When that is not true (all NIFTI),
            //    the call above returns a null dicomResourceCatalog
            // 2. Once you get past that, the code below assumes all DICOM files end with .dcm
            //    One should look through the scan catalog instead.

            if (dicomResourceCatalog == null) {
                return new ArrayList<DicomObject>();
            }
            Path resourceDir = Paths.get(dicomResourceCatalog.getUri()).getParent();
            return Files.list(resourceDir)
                    .filter(path -> path.getFileName().toString().endsWith(".dcm"))
                    .map(path -> _dicomObjectFactory.createDicomObjectQuiet(path.toFile(), false))
                    .collect(Collectors.toList());
        }
        catch (ClientException | IOException e) {
            throw new SearchException( SearchException.Type.UNEXPECTED, "Error getting instances.", e);
        }
    }

    private int getInstanceCount( String archiveRootPath, XnatImagescandataI imageScanData) {
        int count = 0;
        List<DicomObject> instances = new ArrayList<>();
        for( XnatAbstractresourceI resourceI: imageScanData.getFile()) {
            if( XnatResourcecatalog.class.isInstance( resourceI)) {
                XnatResourcecatalog catResource = (XnatResourcecatalog) resourceI;
                if( ("RAW".equals( catResource.getContent()) || "secondary".equals( catResource.getContent())) && "DICOM".equals( catResource.getFormat())) {
                    CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource, null);
                    File catalogFile = CatalogUtils.getCatalogFile( archiveRootPath, catResource);
                    String scanRootPath = catalogFile.getParentFile().getAbsolutePath();
                    if( CatDcmcatalogBean.class.isInstance( catalog1)) {
                        CatDcmcatalogBean dcmcatalog = (CatDcmcatalogBean) catalog1;
                        Collection<CatEntryI> entries = CatalogUtils.getEntriesByFilter(dcmcatalog, new CatalogUtils.CatEntryFilterI() {
                            @Override
                            public boolean accept(CatEntryI entry) {
                                return true;
                            }
                        });
                        count += (entries != null)? entries.size(): 0;
                    }
                }
            }
        }
        return count;
    }

//    private static final String QUERY_GET_INSTANCE_COUNT_IN_SERIES         = "SELECT frame_count FROM xhbm_dicom_instance WHERE imagescandata_id = :scandataId";

}
