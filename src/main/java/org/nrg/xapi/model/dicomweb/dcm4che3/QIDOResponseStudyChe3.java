package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.nrg.xapi.model.dicomweb.QIDOResponseStudy;

import java.util.Date;

public class QIDOResponseStudyChe3 extends QIDOResponseChe3 implements QIDOResponseStudy {

    // TODO: All other Study Level DICOM Attributes passed as {attributeID} query keys that are supported by the service provider as matching or return attributes.
    // TODO: All other Study Level DICOM Attributes passed as "includefield" query values that are supported by the service provider as return attributes
    // TODO: All available Study Level DICOM Attributes if the "includefield" query key is included with a value of "all"


    public String getSpecificCharacterSetString() { return getString( 0x00080005); }
    public void setSpecificCharacterSetString(String value) { setString( 0x00080005, value ); }

    public String getStudyDate() { return getString( 0x00080020); }
    public void setStudyDate(String value) { setString( 0x00080020, value ); }
    public void setStudyDate(Object obj) { setString( 0x00080020, (obj == null)? null:dateFormat.format( obj) ); }

    public String getStudyTime() { return getString(0x00080030); }
    public void setStudyTime(String value) { setString( 0x00080030, value ); }
    public void setStudyTime(Object obj) { setString( 0x00080030, (obj == null)? null:timeFormat.format( obj) ); }

    public String getAccessionNumber() { return getString(0x00080050); }
    public void setAccessionNumber(String value) { setString( 0x00080050, value ); }

    public String getInstanceAvailability() { return getString(0x00080056); }
    public void setInstanceAvailability(String value) { setString( 0x00080056, value ); }

    public String getModalitiesInStudy() { return getString(0x00080061); }
    public void setModalitiesInStudy(String value) { setString( 0x00080061, value ); }

    public String getReferringPhysiciansName() { return getString(0x00080090); }
    public void setReferringPhysiciansName(String value) { setString( 0x00080090, value ); }

    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, value ); }

    public String getPatientsName() { return getString(0x00100010); }
    public void setPatientsName(String value) { setString( 0x00100010, value ); }

    public String getPatientID() { return getString(0x00100020); }
    public void setPatientID(String value) { setString( 0x00100020, value ); }

    public String getPatientsBirthDate() { return getString(0x00100030); }
    public void setPatientsBirthDate(Date value) {
        String v = (value != null)? dateFormat.format( value): null;
        setString( 0x00100030, v ); }
    public void setPatientsBirthDate(String value) { setString( 0x00100030, value ); }

    public String getPatientsSex() { return getString(0x00100040); }
    public void setPatientsSex(String value) {
        String gender = null;
        if( value == null || "".equals(value)) {
            gender = value;
        }
        else {
            switch (value.toLowerCase()) {
                case "male":
                case "m":
                    gender = "M";
                    break;
                case "female":
                case "f":
                    gender = "F";
                    break;
                default:
                    gender = "O";
            }
        }
        setString( 0x00100040, gender );
    }

    public String getStudyInstanceUID() { return getString(0x0020000D); }
    public void setStudyInstanceUID(String value) { setString( 0x0020000D, value ); }

    public String getStudyID() { return getString(0x00200010); }
    public void setStudyID(String value) { setString( 0x00200010, value ); }

//    public String getNumberOfStudyRelatedSeries() { return getString(0x00201206); }
    public int getNumberOfStudyRelatedSeries() { return getInt(0x00201206, 0); }
    public void setNumberOfStudyRelatedSeries(String value) { setString( 0x00201206, value ); }
    public void setNumberOfStudyRelatedSeries(int intValue) { setInt( 0x00201206, intValue ); }

//    public String getNumberOfStudyRelatedInstances() { return getString(0x00201208); }
    public int getNumberOfStudyRelatedInstances() { return getInt(0x00201208, 0); }
    public void setNumberOfStudyRelatedInstances(String value) { setString( 0x00201208, value ); }
    public void setNumberOfStudyRelatedInstances(int intValue) { setInt( 0x00201208, intValue ); }

    public String getRetrieveURLRequestParams() {
        return String.format( "studies/%s", getStudyInstanceUID());
    }

}
