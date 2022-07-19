package org.nrg.xapi.model.dicomweb;

import org.dcm4che3.data.ElementDictionary;

import javax.json.stream.JsonGenerator;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface to keep the underlying DICOM library from leaking.
 */
//@JsonSerialize(using= JsonDicomObjectISerializer.class)
public interface DicomObject {

    int getLength();
    void writeAsPart10 (OutputStream os) throws IOException;
    void writeAsXML( OutputStream os) throws IOException;
    void writeAsJSON( OutputStream os) throws IOException;
    void writeAsJSON( JsonGenerator jsonGenerator) throws IOException;

    String getString( int tag);
    void setString( int tag, String value);

    int getInt( int tag, int def);
    void setInt( int tag, int value);

    byte[] getBytes( int tag) throws IOException;
}
