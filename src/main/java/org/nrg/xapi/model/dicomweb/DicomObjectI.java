package org.nrg.xapi.model.dicomweb;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nrg.xapi.rest.dicomweb.JsonDicomObjectISerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to keep the underlying DICOM library from leaking.
 */
@JsonSerialize(using= JsonDicomObjectISerializer.class)
public interface DicomObjectI {

    void write(OutputStream os) throws IOException;

    InputStream getInputStream() throws IOException;

    File getFile() ;

    String getString( int tag);
    byte[] getBytes( int tag) throws IOException;

    byte[] getPixels() throws IOException;
    String getTransferSyntaxUID();
    String getStudyInstanceUID();
    String getSeriesInstanceUID();
    String getSOPInstanceUID();
    String getSOPClassUID();
    Integer getInstanceNumber();
    Integer getRows();
    Integer getColumns();
    Integer getBitsAllocated();
    Integer getNumberOfFrames();
    Integer getFrameNumber();
    String getImagePositionPatient();
    String getImageOrientationPatient();
    String getPixelSpacing();
    String getFrameOfReferenceUid();

}
