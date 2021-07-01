package org.nrg.xapi.model.dicomweb;

import java.util.Date;

public interface QIDOResponseStudySeries extends QIDOResponse {

    // Study
    String getSpecificCharacterSetString();
    void setSpecificCharacterSetString(String value);

    String getStudyDate();
    void setStudyDate(String value);
    void setStudyDate(Object obj);

    String getStudyTime();
    void setStudyTime(String value);
    void setStudyTime(Object obj);

    String getAccessionNumber();
    void setAccessionNumber(String value);

    String getInstanceAvailability();
    void setInstanceAvailability(String value);

    String getModalitiesInStudy();
    void setModalitiesInStudy(String value);

    String getReferringPhysiciansName();
    void setReferringPhysiciansName(String value);

    String getTimezoneOffsetFromUTC();
    void setTimezoneOffsetFromUTC(String value);

    String getPatientsName();
    void setPatientsName(String value);

    String getPatientID();
    void setPatientID(String value);

    String getPatientsBirthDate();
    void setPatientsBirthDate(Date value);
    void setPatientsBirthDate(String value);

    String getPatientsSex();
    void setPatientsSex(String value);

    String getStudyInstanceUID();
    void setStudyInstanceUID(String value);

    String getStudyID();
    void setStudyID(String value);

    int getNumberOfStudyRelatedSeries();
    void setNumberOfStudyRelatedSeries(String value);
    void setNumberOfStudyRelatedSeries(int intValue);

    int getNumberOfStudyRelatedInstances();
    void setNumberOfStudyRelatedInstances(String value);
    void setNumberOfStudyRelatedInstances(int intValue);

    // Series
    String getModality();
    void setModality(String value);

    String getSeriesDescription();
    void setSeriesDescription(String value);

//    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
//    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, ElementDictionary.vrOf(0x00080201, null), value ); }

    String getSeriesInstanceUID();
    void setSeriesInstanceUID(String value);

    String getSeriesNumber();
    void setSeriesNumber(String value);

    String getNumberOfSeriesRelatedInstances();
    void setNumberOfSeriesRelatedInstances(int value);

    String getPerformedProcedureStepStartDate();
    void setPerformedProcedureStepStartDate(String value);
    void setPerformedProcedureStepStartDate(Object obj);

    String getPerformedProcedureStepStartTime();
    void setPerformedProcedureStepStartTime(String value);
    void setPerformedProcedureStepStartTime(Object obj);

}
