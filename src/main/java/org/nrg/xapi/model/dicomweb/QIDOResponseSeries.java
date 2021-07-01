package org.nrg.xapi.model.dicomweb;

public interface QIDOResponseSeries extends QIDOResponse {

    String getSpecificCharacterSetString();
    void setSpecificCharacterSetString(String value);

    String getModality();
    void setModality(String value);

    String getSeriesDescription();
    void setSeriesDescription(String value);

    String getTimezoneOffsetFromUTC();
    void setTimezoneOffsetFromUTC(String value);

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
