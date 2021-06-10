package org.nrg.xapi.rest.dicomweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * Translate parameter names between the DICOM and XNAT domains.
 *
 *
 */
public class QueryParameters extends BaseQueryParameters {
    public static final String STUDY_INSTANCE_UID_NAME = "studyInstanceUID";
    public static final String STUDY_DATE_NAME = "studyDate";
    public static final String STUDY_TIME_NAME = "studyTime";
    public static final String MODALITIES_IN_STUDY_NAME = "modalitiesInStudy";
    public static final String REFERRING_PHYSICIAN_NAME_NAME = "referringPhysicianName";
    public static final String PATIENT_ID_NAME = "patientID";
    public static final String PATIENT_NAME_NAME = "patientName";
    public static final String ACCESSION_NUMBER_NAME = "accessionNumber";
    public static final String STUDY_ID_NAME = "studyID";

    public static final String MODALITY_NAME = "modality";
    public static final String SERIES_INSTANCE_UID_NAME = "seriesInstanceUID";
    public static final String SERIES_NUMBER_NAME = "seriesNumber";
    public static final String PERFORMED_PROCEDURE_STEP_STARTDATE = "PerformedProcedureStepStartDate";
    public static final String PERFORMED_PROCEDURE_STEP_STARTTIME = "PerformedProcedureStepStartTime";

    public static final String SOP_INSTANCE_UID_NAME = "sopInstanceUID";
    public static final String SOP_CLASS_UID_NAME = "sopClassUID";
    public static final String INSTANCE_NUMBER_NAME = "instanceNumber";

    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";

    private static final Logger _log = LoggerFactory.getLogger(QueryParameters.class);

    public QueryParameters( MultiValueMap<String, String> dicomRequestParams) {
        super(dicomRequestParams);
    }

    /**
     * Add the value of the DICOM-named parameter to the map.  It will be stored under its normalized name.
     *
     * @param dicomParamName
     * @param value
     */
    public void addDicomParameter(String dicomParamName, List<String> value) {
        switch( dicomParamName.toLowerCase()) {
            case "studyinstanceuid":
            case "0020000d":
                value.forEach( v -> {
                    List<String> uids = parseUIDs(v);
                    for (String uid : uids) {
                        addParam(STUDY_INSTANCE_UID_NAME, uid);
                    }
                });
                break;
            case "studydate":
            case "00080020":
                value.forEach( v -> addParam( STUDY_DATE_NAME, v));
                break;
            case "studytime":
            case "00080030":
                value.forEach( v -> addParam( STUDY_TIME_NAME, v));
                break;
            case "modalitiesinstudy":
            case "00080061":
                value.forEach( v -> addParam( MODALITIES_IN_STUDY_NAME, v));
                break;
            case "referringphysicianname":
            case "00080090":
                value.forEach( v -> addParam( REFERRING_PHYSICIAN_NAME_NAME, v));
                break;
            case "00100020":
            case "patientid":
                value.forEach( v -> addParam( PATIENT_ID_NAME, v));
                break;
            case "00100010":
            case "patientname":
                value.forEach( v -> addParam( PATIENT_NAME_NAME, v));
                break;
            case "00080050":
            case "accessionnumber":
                value.forEach( v -> addParam( ACCESSION_NUMBER_NAME, v));
                break;
            case "00200010":
            case "studyid":
                value.forEach( v -> addParam( STUDY_ID_NAME, v));
                break;

            case "seriesinstanceuid":
            case "0020000E":
                value.forEach( v -> {
                    List<String> uids = parseUIDs(v);
                    for (String uid : uids) {
                        addParam(SERIES_INSTANCE_UID_NAME, uid);
                    }
                });
                break;
            case "modality":
            case "00080060":
                value.forEach( v -> addParam( MODALITY_NAME, v));
                break;
            case "seriesnumber":
            case "00200011":
                value.forEach( v -> addParam( SERIES_NUMBER_NAME, v));
                break;
            case "performedprocedurestepstartdate":
            case "00400244":
                value.forEach( v -> addParam( PERFORMED_PROCEDURE_STEP_STARTDATE, v));
                break;
            case "performedprocedurestepstarttime":
            case "00400245":
                value.forEach( v -> addParam( PERFORMED_PROCEDURE_STEP_STARTTIME, v));
                break;

            case "sopinstanceuid":
            case "00080018":
                value.forEach( v -> addParam( SOP_INSTANCE_UID_NAME, v));
                break;
            case "sopclassuid":
            case "00080016":
                value.forEach( v -> addParam( SOP_CLASS_UID_NAME, v));
                break;
            case "instancenumber":
            case "00200013":
                value.forEach( v -> addParam( INSTANCE_NUMBER_NAME, v));
                break;

            case "limit":
                value.forEach( v -> addParam( LIMIT, v));
                break;
            case "offset":
                value.forEach( v -> addParam( OFFSET, v));
                break;
            default:
                _log.warn("Ignoring unrecognized series/study-level query parameter: " + dicomParamName + " = " + value);
        }
    }

    public boolean hasStudyLevel() {
        for( String key: keySet()) {
            switch( key) {
                case STUDY_INSTANCE_UID_NAME:
                case STUDY_DATE_NAME:
                case STUDY_TIME_NAME:
                case MODALITIES_IN_STUDY_NAME:
                case REFERRING_PHYSICIAN_NAME_NAME:
                case PATIENT_ID_NAME:
                case PATIENT_NAME_NAME:
                case ACCESSION_NUMBER_NAME:
                case STUDY_ID_NAME:
                    return true;
            }
        }
        return false;
    }

    public boolean hasStudyDate() {
        for( String key: keySet()) {
            switch( key) {
                case STUDY_DATE_NAME:
                    return true;
            }
        }
        return false;
    }

    public String getStudyDateRange() {
        List<String> params = getParams( STUDY_DATE_NAME);
        return (params.isEmpty())? null: params.get(0);
    }

    public boolean hasStudyTime() {
        for( String key: keySet()) {
            switch( key) {
                case STUDY_TIME_NAME:
                    return true;
            }
        }
        return false;
    }

    public String getStudyTimeRange() {
        List<String> params = getParams( STUDY_TIME_NAME);
        return (params.isEmpty())? null: params.get(0);
    }

    public boolean hasSeriesLevel() {
        for( String key: keySet()) {
            switch( key) {
                case MODALITY_NAME:
                case SERIES_INSTANCE_UID_NAME:
                case SERIES_NUMBER_NAME:
                case PERFORMED_PROCEDURE_STEP_STARTDATE:
                case PERFORMED_PROCEDURE_STEP_STARTTIME:
                    return true;
            }
        }
        return false;
    }

    /**
     * Return the value of the Limit qyery parameter.
     *
     * @return the value of the limit qyery parameter, MAX-INT if limit is not present or empty.
     * @throws IllegalArgumentException if limit parameter appears multiple times.
     * @throws IllegalArgumentException if limit parameter is outside the range of 1 to MAX-INT.
     * @throws NumberFormatException if value is not a parsable integer.
     */
    public int getLimit() {
        int limit;
        List<String> values = getParams( LIMIT);
        if( values == null || values.isEmpty()) {
            limit = Integer.MAX_VALUE;
        }
        else if( values.size() == 1) {
            limit = Integer.parseInt( values.get(0));
            if( limit < 1) {
                throw new IllegalArgumentException("Limit is out of range: " + limit);
            }
        }
        else {
            throw new IllegalArgumentException("Limit parameter may not appear multiple times.");
        }
        return limit;
    }

    /**
     * Return the value of the Offset qyery parameter.
     *
     * @return the value of the offset qyery parameter, 0 if limit is not present or empty.
     * @throws IllegalArgumentException if offset parameter appears multiple times.
     * @throws IllegalArgumentException if offset parameter is outside the range of 1 to MAX-INT.
     * @throws NumberFormatException if value is not a parsable integer.
     */
    public int getOffset() {
        int offset;
        List<String> values = getParams( OFFSET);
        if( values == null || values.isEmpty()) {
            offset = 0;
        }
        else if( values.size() == 1) {
            offset = Integer.parseInt( values.get(0));
            if( offset < 0) {
                throw new IllegalArgumentException("Offset is out of range: " + offset);
            }
        }
        else {
            throw new IllegalArgumentException("Offset parameter may not appear multiple times.");
        }
        return offset;
    }

}
