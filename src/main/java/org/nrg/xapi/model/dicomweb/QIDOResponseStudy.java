package org.nrg.xapi.model.dicomweb;

import java.util.Date;

public interface QIDOResponseStudy extends QIDOResponse {

    // TODO: All other Study Level DICOM Attributes passed as {attributeID} query keys that are supported by the service provider as matching or return attributes.
    // TODO: All other Study Level DICOM Attributes passed as "includefield" query values that are supported by the service provider as return attributes
    // TODO: All available Study Level DICOM Attributes if the "includefield" query key is included with a value of "all"

    String getSpecificCharacterSetString();
    void setSpecificCharacterSetString(String value);

    String getStudyDate();
    void setStudyDate(String value);
    void setStudyDate(Object obj);

    String getStudyTime();
    void setStudyTime(String value) ;
    void setStudyTime(Object obj) ;

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

//    String getNumberOfStudyRelatedSeries();
    int getNumberOfStudyRelatedSeries();
    void setNumberOfStudyRelatedSeries(String value);
    void setNumberOfStudyRelatedSeries(int intValue);

//    String getNumberOfStudyRelatedInstances();
    int getNumberOfStudyRelatedInstances();
    void setNumberOfStudyRelatedInstances(String value);
    void setNumberOfStudyRelatedInstances(int intValue);

    String getRetrieveURLRequestParams();

}
