package org.nrg.xapi.model.dicomweb;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nrg.xapi.rest.dicomweb.JsonDicomObjectSerializer;
import org.nrg.xapi.rest.dicomweb.JsonQIDOResponseSerializer;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;

/**
 * Created by davidmaffitt on 8/3/17.
 */
@XmlRootElement
//@JsonSerialize(using= JsonQIDOResponseSerializer.class)
@JsonSerialize(using= JsonDicomObjectSerializer.class)
public interface QIDOResponse extends DicomObject {

    // TODO: All other Study Level DICOM Attributes passed as {attributeID} query keys that are supported by the service provider as matching or return attributes.
    // TODO: All other Study Level DICOM Attributes passed as "includefield" query values that are supported by the service provider as return attributes
    // TODO: All available Study Level DICOM Attributes if the "includefield" query key is included with a value of "all"


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

    String getRetrieveURL();
    void setRetrieveURL(String value);

}
