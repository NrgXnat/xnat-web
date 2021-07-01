package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.nrg.xapi.model.dicomweb.QIDOResponseSeries;

public class QIDOResponseSeriesChe3 extends QIDOResponseChe3 implements QIDOResponseSeries {

    public String getSpecificCharacterSetString() { return getString( 0x00080005); }
    public void setSpecificCharacterSetString(String value) { setString( 0x00080005, value ); }

    public String getModality() { return getString( 0x00080060); }
    public void setModality(String value) { setString( 0x00080060, value ); }

    public String getSeriesDescription() { return getString(0x0008103E); }
    public void setSeriesDescription(String value) { setString( 0x0008103E, value ); }

    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, value ); }

    public String getSeriesInstanceUID() { return getString(0x0020000E); }
    public void setSeriesInstanceUID(String value) { setString( 0x0020000E, value ); }

    public String getSeriesNumber() { return getString(0x00200011); }
    public void setSeriesNumber(String value) { setString( 0x00200011, value ); }

    public String getNumberOfSeriesRelatedInstances() { return getString(0x00201209); }
    public void setNumberOfSeriesRelatedInstances(int value) { setInt( 0x00201209, value ); }

    public String getPerformedProcedureStepStartDate() { return getString(0x00400244); }
    public void setPerformedProcedureStepStartDate(String value) { setString( 0x00400244, value ); }
    public void setPerformedProcedureStepStartDate(Object obj) { setString( 0x00400244, (obj == null)? null:dateFormat.format( obj) ); }

    public String getPerformedProcedureStepStartTime() { return getString(0x00400245); }
    public void setPerformedProcedureStepStartTime(String value) { setString( 0x00400245, value ); }
    public void setPerformedProcedureStepStartTime(Object obj) { setString( 0x00400245, (obj == null)? null:timeFormat.format( obj) ); }
}
