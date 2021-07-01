package org.nrg.xapi.model.dicomweb;

public interface QIDOResponseInstance extends QIDOResponse {

    String getSpecificCharacterSetString();
    void setSpecificCharacterSetString(String value);

    String getSopInstanceUID();
    void setSopInstanceUID(String value);

    String getSopClassUID();
    void setSopClassUID(String value);

    String getTimezoneOffsetFromUTC();
    void setTimezoneOffsetFromUTC(String value);

    String getInstanceNumber();
    void setInstanceNumber(String value);

    String getRows();
    void setRows(String value);

    String getCols();
    void setCols(String value);

    String getBitsAllocated();
    void setBitsAllocated(int value);

    String getNumberOfFrames();
    void setNumberOfFrames(int value);

}
