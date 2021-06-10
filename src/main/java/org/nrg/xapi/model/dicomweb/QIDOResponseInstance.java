package org.nrg.xapi.model.dicomweb;

import org.dcm4che3.data.ElementDictionary;

public class QIDOResponseInstance extends QIDOResponse {


    public String getSpecificCharacterSetString() { return getString( 0x00080005); }
    public void setSpecificCharacterSetString(String value) { setString( 0x00080005, ElementDictionary.vrOf(0x00080005,null), value ); }

    public String getSopInstanceUID() { return getString( 0x00080018); }
    public void setSopInstanceUID(String value) { setString( 0x00080018, ElementDictionary.vrOf(0x00080018, null), value ); }

    public String getSopClassUID() { return getString( 0x00080016); }
    public void setSopClassUID(String value) { setString( 0x00080016, ElementDictionary.vrOf(0x00080016, null), value ); }

    public String getTimezoneOffsetFromUTC() { return getString(0x00080201); }
    public void setTimezoneOffsetFromUTC(String value) { setString( 0x00080201, ElementDictionary.vrOf(0x00080201, null), value ); }

    public String getInstanceNumber() { return getString(0x00200013); }
    public void setInstanceNumber(String value) { setString( 0x00200013, ElementDictionary.vrOf(0x00200013, null), value ); }

    public String getRows() { return getString(0x00280010); }
    public void setRows(String value) { setString( 0x00280010, ElementDictionary.vrOf(0x00280010, null), value ); }

    public String getCols() { return getString(0x00280011); }
    public void setCols(String value) { setString( 0x00280011, ElementDictionary.vrOf(0x00280011, null), value ); }

    public String getBitsAllocated() { return getString(0x00280100); }
    public void setBitsAllocated(int value) { setInt( 0x00280100, ElementDictionary.vrOf(0x00280100, null), value ); }

    public String getNumberOfFrames() { return getString(0x00280008); }
    public void setNumberOfFrames(int value) { setInt( 0x00280008, ElementDictionary.vrOf(0x00280008, null), value ); }

}
