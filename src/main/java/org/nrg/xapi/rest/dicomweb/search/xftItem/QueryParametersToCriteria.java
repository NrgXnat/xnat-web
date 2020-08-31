package org.nrg.xapi.rest.dicomweb.search.xftItem;

import org.nrg.xapi.rest.dicomweb.BaseQueryParameters;
import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xft.search.CriteriaCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class to map DICOMweb query parameters into Critteria Collections used by ItemSearch to execute the query.
 *
 */
public class QueryParametersToCriteria {

    private static final Logger _log = LoggerFactory.getLogger(XftSearchEngine.class);

    public static CriteriaCollection mapSeries( String studyInstanceUID, BaseQueryParameters params) {
        CriteriaCollection cc = mapSeries( params);

        cc.addClause("xnat:imagesessiondata/uid", studyInstanceUID);
        return cc;
    }

    public static CriteriaCollection mapStudy(BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");
        for (String paramName: params.keySet() ) {
            switch( paramName) {
                case QueryParameters.STUDY_DATE_NAME:
//                    cc.addClause( "xnat:experimentData/date", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseDateCriteria( params.getParams( paramName).get(0)));
                    break;
                case QueryParameters.STUDY_TIME_NAME:
//                    cc.addClause( "xnat:experimentData/time", "=" , queryParameters.getParams( paramName).get(0));
                    cc.addClause( parseTimeCriteria( params.getParams( paramName).get(0)));
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

        if( cc.size() == 0) {
            cc.addClause( "xnat:imagesessionData/dcmaccessionnumber", "like" , "*");
        }

        return cc;
    }


    public static CriteriaCollection mapSeries( BaseQueryParameters params) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        for (String paramName : params.keySet()) {
            switch (paramName) {
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTDATE:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/start_date", params.getParams(paramName).get(0)));
                    break;
                case QueryParameters.PERFORMED_PROCEDURE_STEP_STARTTIME:
                    cc.addClause(parseRangeCriteria("xnat:imagescandata/starttime", params.getParams(paramName).get(0)));
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

    private static CriteriaCollection parseAccessionNumberCriteria( String accessionNumberString) {
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

    private static CriteriaCollection parseDateCriteria( String dateString) {
        return parseRangeCriteria( "xnat:experimentData/date", dateString);
    }

    private static CriteriaCollection parseTimeCriteria( String timeString) {
        return parseRangeCriteria( "xnat:experimentData/time", timeString);
    }

    private static CriteriaCollection parseRangeCriteria( String xmlPath, String value) {
        CriteriaCollection cc = new CriteriaCollection("AND");

        if( value.contains("-")) {
            if( value.startsWith("-")) {
                cc.addClause( xmlPath, "<=" , normalizedTimeString( value.replaceAll("[- ]","")));
            }
            else if( value.endsWith("-")) {
                cc.addClause( xmlPath, ">=" , normalizedTimeString( value.replaceAll("[- ]","")));
            }
            else {
                String[] dates = value.split("-");
                cc.addClause( xmlPath, ">=" , normalizedTimeString( dates[0]));
                cc.addClause( xmlPath, "<=" , normalizedTimeString( dates[1]));
            }
        }
        else {
            cc.addClause( xmlPath, "=" , value);
        }
        return cc;
    }

    /**
     * Return Time String in format HHMMSS or HHMMSS.FFFFFF where fractional digits F are 1 to 6 in number.
     *
     * The DICOM Time VR is allowed to truncate MM, SS, or FFFFFF. This is ISO 8601 compliant but this can confuse downstream SQL in Criteria.
     * Normalize the DICOM Time string to the more complete ISO 8601 time format by padding with zeros.
     *
     * @param dicomTimeString
     * @return
     */
    public static String normalizedTimeString( String dicomTimeString) {
        String time = "";
        String fractionalSeconds = "";
        if( dicomTimeString.contains(".")) {
            String[] tokens = dicomTimeString.split( Pattern.quote("."));
            time = tokens[0].trim();
            fractionalSeconds = tokens[1].trim();
        }
        else {
            time = dicomTimeString.trim();
        }
        String value = String.format("%1$-6s", time).replace(' ', '0');
        if( ! fractionalSeconds.isEmpty()) {
            value = value + "." + fractionalSeconds;
        }
        return value;
    }

    private static  CriteriaCollection parsePatientNameCriteria( String pName) {
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

    private static  CriteriaCollection parsePatientIDCriteria( String pID) {
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

    private static List<String> getModalities( String modalitiesString) {
        List<String> modalities =  (modalitiesString != null)? parseMultivaluedValue( modalitiesString): new ArrayList<String>();
        return modalities;
    }

    private static List<String> parseMultivaluedValue(String value) {
        return Arrays.asList( value.split("\\\\"));
    }


}
