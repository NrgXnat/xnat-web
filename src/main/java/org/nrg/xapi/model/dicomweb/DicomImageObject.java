package org.nrg.xapi.model.dicomweb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to keep the underlying DICOM library from leaking.
 */
//@JsonSerialize(using= JsonDicomObjectISerializer.class)
public interface DicomImageObject extends DicomObject {

    File getFile() ;
    InputStream getInputStream() throws IOException;
    void writeFile( OutputStream os) throws IOException;

    byte[] getPixelsForFrame( int frameNumber) throws IOException;

    void seekToFrame(int frameNumber) throws IOException;
    int getCurrentFrame();
    int getCurrentFrameLength();

    int getPixelDataLength() throws IOException;
    int getPixelDataLength( int frame) throws IOException;

    void writePixelData(OutputStream os) throws IOException;
    void writePixelData(int frame, OutputStream os) throws IOException;
    void writePixelDataRandomFrame(int frame, OutputStream os) throws IOException;

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
    String getModality();

}
