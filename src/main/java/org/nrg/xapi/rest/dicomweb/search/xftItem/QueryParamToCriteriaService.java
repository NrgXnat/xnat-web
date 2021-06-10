package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.xapi.rest.dicomweb.BaseQueryParameters;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xft.search.CriteriaCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class to map DICOMweb query parameters into Critteria Collections used by ItemSearch to execute the query.
 *
 */
public class QueryParamToCriteriaService {

    private DateTimeService _dateTimeService;
    private static final Logger _log = LoggerFactory.getLogger( "dicomweb");

    public QueryParamToCriteriaService( DateTimeService dateTimeService) {
        this._dateTimeService = dateTimeService;
    }

    public CriteriaCollection mapStudy( String sessionID, BaseQueryParameters params) {
        CriteriaCollection cc = mapStudy( params);
        if( ! (sessionID == null || sessionID.isEmpty())) {
            cc.addClause( "xnat:imagesessiondata/id", "=", sessionID);
        }
        return cc;
    }

    public CriteriaCollection mapStudy(BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

        for (String paramName: params.keySet() ) {
            switch( paramName) {
                case QueryParameters.STUDY_DATE_NAME:
//                    cc.addClause( "xnat:experimentData/date", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseDateCriteria( params.getParams( paramName).get(0), zoneOffset));
                    break;
                case QueryParameters.STUDY_TIME_NAME:
//                    cc.addClause( "xnat:experimentData/time", "=" , queryParameters.getParams( paramName).get(0));
                    // Add criteria for times only if the query is not also for dates. We will do combined date-time matching
                    // by filtering responses after the item search.
                    if( params.getParams( QueryParameters.STUDY_DATE_NAME) == null) {
                        cc.addClause( parseTimeCriteria( params.getParams( paramName).get(0), zoneOffset));
                    }
                    break;
                case QueryParameters.STUDY_ID_NAME:
                    String value = params.getParams( paramName).get(0);
                    if( ! ("*".equals( value) || "all".equals( value))) {
                        cc.addClause("xnat:imagesessionData/study_id", "=", value);
                    }
                    break;
                case QueryParameters.STUDY_INSTANCE_UID_NAME:
                    List<String> uidListList = params.getParams( paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for( String uidList: uidListList) {
                        if (uidList != null) {
                            for (String uid : uidList.split(",")) {
                                cc_or_uid.addClause("xnat:imagesessiondata/uid", "=", uid.trim());
                            }
                            cc.addClause(cc_or_uid);
                        }
                    }
                    break;
                case QueryParameters.REFERRING_PHYSICIAN_NAME_NAME:
                    _log.warn("Study-level query parameter ReferringPhysicianName is not supported.");
                    break;
                case QueryParameters.PATIENT_ID_NAME:
                    value = params.getParams( paramName).get(0);
                    if( ! ("*".equals( value) || "all".equals( value))) {
                        cc.addClause(parsePatientIDCriteria( value));
                    }
                    break;
                case QueryParameters.PATIENT_NAME_NAME:
                    value = params.getParams( paramName).get(0);
                    if( ! ("*".equals( value) || "all".equals( value))) {
                        cc.addClause(parsePatientNameCriteria(value));
                    }
                    break;
                case QueryParameters.ACCESSION_NUMBER_NAME:
                    value = params.getParams( paramName).get(0);
                    if( ! ("*".equals( value) || "all".equals( value))) {
                        cc.addClause(parseAccessionNumberCriteria( value));
                    }
                    break;
                case QueryParameters.MODALITIES_IN_STUDY_NAME:
                    List<String> modalities = getModalities( params.getParams( paramName).get(0));

                    // Neither the straight AND or OR do the right thing.
//                    CriteriaCollection cc_and = new CriteriaCollection("AND");
                    CriteriaCollection cc_or = new CriteriaCollection("OR");
                    for( String modality: modalities) {
//                        cc_and.addClause( "xnat:imagescanData/modality", "=", modality);
                        cc_or.addClause( "xnat:imagescanData/modality", "=", modality);
                    }
//                    cc.addClause( cc_and);
                    cc.addClause( cc_or);
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + params.asString(paramName));
                    break;
            }
        }

        return cc;
    }

    public CriteriaCollection mapSeries( String sessionID, String studyInstanceUID, BaseQueryParameters params) {
        CriteriaCollection cc = mapSeries( sessionID, params);

        cc.addClause("xnat:imagesessiondata/uid", studyInstanceUID);
        return cc;
    }

    /*
        Query parms don't come into play here because we need to search scan catalogs.  But another implementation
        will use them.
        This should return an imagesession with the unique series.
     */
    public CriteriaCollection mapInstances( String sessionID, String studyInstanceUID, String seriesInstanceUID, BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");
        if( ! (sessionID == null || sessionID.isEmpty())) {
            cc.addClause( "xnat:experimentData/id", "=", sessionID);
        }
        cc.addClause("xnat:imagesessiondata/uid", studyInstanceUID);
        cc.addClause("xnat:imagescandata/uid", seriesInstanceUID);
        return cc;
    }

    public CriteriaCollection mapSeries( String sessionID, BaseQueryParameters params) {
        CriteriaCollection cc = mapSeries( params);
        if( ! (sessionID == null || sessionID.isEmpty())) {
            cc.addClause( "xnat:experimentData/id", "=", sessionID);
        }
        return cc;
    }

    private CriteriaCollection mapSeries( BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

        for (String paramName : params.keySet()) {
            switch (paramName) {
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTDATE:
                    cc.addClause(parseDateRangeCriteria("xnat:imagescandata/start_date", params.getParams(paramName).get(0), zoneOffset));
                    break;
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTTIME:
                    // Add criteria for times only if the query is not also for dates. We will do combined date-time matching
                    // by filtering responses after the item search.
                    if( params.getParams( QueryParameters.PERFORMED_PROCEDURE_STEP_STARTDATE) == null) {
                        cc.addClause( parseTimeCriteria( params.getParams( paramName).get(0), zoneOffset));
                    }
                    // Don't add criteria for times. Filter responses after the item search.
//                    cc.addClause(parseRangeCriteria("xnat:imagescandata/starttime", params.getParams(paramName).get(0)));
                    break;
                case QueryParameters.SERIES_NUMBER_NAME:
                    cc.addClause("xnat:imagescanData/id", "=", params.getParams(paramName).get(0));
                    break;
                case QueryParameters.SERIES_INSTANCE_UID_NAME:
                    List<String> uids = params.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for (String uid : uids) {
                        cc_or_uid.addClause("xnat:imagescandata/uid", "=", uid);
                    }
                    cc.addClause(cc_or_uid);
                    break;
                case QueryParameters.MODALITY_NAME:
                    cc.addClause("xnat:imagescanData/modality", "=", params.getParams(paramName).get(0));
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + params.asString(paramName));
                    break;
            }
        }
        return cc;
    }

    private CriteriaCollection mapInstances( BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

        for (String paramName : params.keySet()) {
            switch (paramName) {
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTDATE:
                    cc.addClause(parseDateRangeCriteria("xnat:imagescandata/start_date", params.getParams(paramName).get(0), zoneOffset));
                    break;
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTTIME:
                    // Add criteria for times only if the query is not also for dates. We will do combined date-time matching
                    // by filtering responses after the item search.
                    if( params.getParams( QueryParameters.PERFORMED_PROCEDURE_STEP_STARTDATE) == null) {
                        cc.addClause( parseTimeCriteria( params.getParams( paramName).get(0), zoneOffset));
                    }
                    // Don't add criteria for times. Filter responses after the item search.
//                    cc.addClause(parseRangeCriteria("xnat:imagescandata/starttime", params.getParams(paramName).get(0)));
                    break;
                case QueryParameters.SERIES_NUMBER_NAME:
                    cc.addClause("xnat:imagescanData/id", "=", params.getParams(paramName).get(0));
                    break;
                case QueryParameters.SERIES_INSTANCE_UID_NAME:
                    List<String> uids = params.getParams(paramName);
                    CriteriaCollection cc_or_uid = new CriteriaCollection("OR");
                    for (String uid : uids) {
                        cc_or_uid.addClause("xnat:imagescandata/uid", "=", uid);
                    }
                    cc.addClause(cc_or_uid);
                    break;
                case QueryParameters.MODALITY_NAME:
                    cc.addClause("xnat:imagescanData/modality", "=", params.getParams(paramName).get(0));
                    break;
                default:
                    _log.warn("Ignoring query parameter: " + params.asString(paramName));
                    break;
            }
        }
        return cc;
    }

    private CriteriaCollection parseAccessionNumberCriteria( String accessionNumberString) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        if( accessionNumberString.contains("*") || accessionNumberString.contains("?")) {
            String value = accessionNumberString.replaceAll( Pattern.quote( "*"), "%");
            value = value.replaceAll( Pattern.quote( "?"), "_");
            cc.addClause( "xnat:imagesessionData/dcmaccessionnumber", "LIKE" , value);
        }
        else {
            cc.addClause( "xnat:imagesessionData/dcmaccessionnumber", "=" , accessionNumberString);
        }
        return cc;
    }

    private CriteriaCollection parseDateCriteria(String dateString, ZoneOffset zoneOffset) {
        return parseDateRangeCriteria( "xnat:experimentData/date", dateString, zoneOffset);
    }

    private CriteriaCollection parseTimeCriteria( String timeString, ZoneOffset zoneOffset) {
        return parseTimeRangeCriteria( "xnat:experimentData/time", timeString, zoneOffset);
    }

    private CriteriaCollection parseDateRangeCriteria( String xmlPath, String value, ZoneOffset zoneOffset) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        Interval interval = _dateTimeService.parseDicomDateRangeString( value, zoneOffset);
        if( interval.isZeroDuration()) {
            cc.addClause( xmlPath, "=" , interval.getStartDateString());
        }
        else {
            if (interval.isOpenStartDate()) {
                cc.addClause(xmlPath, "<=", interval.getEndDateString());
            } else if (interval.isOpenEndDate()) {
                cc.addClause(xmlPath, ">=", interval.getStartDateString());
            } else {
                cc.addClause(xmlPath, ">=", interval.getStartDateString());
                cc.addClause(xmlPath, "<=", interval.getEndDateString());
            }
        }
        return cc;
    }

    private CriteriaCollection parseTimeRangeCriteria( String xmlPath, String value, ZoneOffset zoneOffset) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        Interval interval = _dateTimeService.parseDicomTimeRangeString( value, zoneOffset);
        if( interval.isZeroDuration()) {
            cc.addClause( xmlPath, "=" , interval.getStartTimeString());
        }
        else {
            if (interval.isOpenStartTime()) {
                cc.addClause(xmlPath, "<=", interval.getEndTimeString());
            } else if (interval.isOpenEndTime()) {
                cc.addClause(xmlPath, ">=", interval.getStartTimeString());
            } else {
                cc.addClause(xmlPath, ">=", interval.getStartTimeString());
                cc.addClause(xmlPath, "<=", interval.getEndTimeString());
            }
        }
        return cc;
    }

    private CriteriaCollection parsePatientNameCriteria( String pName) {
        CriteriaCollection cc = new CriteriaCollection( "AND");
        if( pName.contains("*") || pName.contains("?")) {
            String value = pName.replaceAll( Pattern.quote( "*"), "%");
            value = value.replaceAll( Pattern.quote( "?"), "_");
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "LIKE" , value);
        }
        else {
            cc.addClause( "xnat:imagesessionData/dcmpatientname", "=", pName);
        }
        return cc;
    }

    private CriteriaCollection parsePatientIDCriteria( String pID) {
        CriteriaCollection cc = new CriteriaCollection( "AND");
        if( pID.contains("*") || pID.contains("?")) {
            String value = pID.replaceAll( Pattern.quote( "*"), "%");
            value = value.replaceAll( Pattern.quote( "?"), "_");
            cc.addClause( "xnat:imagesessionData/dcmpatientid", "LIKE" , value);
        }
        else {
            cc.addClause( "xnat:imagesessionData/dcmpatientid", "=", pID);
        }
        return cc;
    }

    private List<String> getModalities( String modalitiesString) {
        List<String> modalities =  (modalitiesString != null)? parseMultivaluedValue( modalitiesString): new ArrayList<String>();
        return modalities;
    }

    private List<String> parseMultivaluedValue(String value) {
        return Arrays.asList( value.split("\\\\"));
    }

}
