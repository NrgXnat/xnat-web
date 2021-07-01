package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.nrg.xapi.model.dicomweb.QIDOResponseInstance;

public class QIDOResponseInstanceChe3 extends QIDOResponseChe3 implements QIDOResponseInstance {

    public String getSpecificCharacterSetString() { return getString( 0x00080005); }
    public void setSpecificCharacterSetString(String value) { setString( 0x00080005, value ); }

    public String getSopInstanceUID() { return getString( 0x00080018); }
    public void setSopInstanceUID(String value) { setString( 0x00080018, value ); }

    public String getSopClassUID() { return getString( 0x00080016); }
    public void setSopClassUID(String value) { setString( 0x00080016, value ); }

    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, value ); }

    public String getInstanceNumber() { return getString(0x00200013); }
    public void setInstanceNumber(String value) { setString( 0x00200013, value ); }

    public String getRows() { return getString(0x00280010); }
    public void setRows(String value) { setString( 0x00280010, value ); }

    public String getCols() { return getString(0x00280011); }
    public void setCols(String value) { setString( 0x00280011, value ); }

    public String getBitsAllocated() { return getString(0x00280100); }
    public void setBitsAllocated(int value) { setInt( 0x00280100, value ); }

    public String getNumberOfFrames() { return getString(0x00280008); }
    public void setNumberOfFrames(int value) { setInt( 0x00280008, value ); }

}
