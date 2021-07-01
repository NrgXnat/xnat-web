package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.nrg.xapi.model.dicomweb.QIDOResponse;

public class QIDOResponseChe3 extends DicomObjectChe3 implements QIDOResponse {

    public QIDOResponseChe3() {
        super();
    }

    public String getRetrieveURL() {
        return getString(0x00081190);
    }

    public void setRetrieveURL(String value) {
        setString(0x00081190, value);
    }

}
