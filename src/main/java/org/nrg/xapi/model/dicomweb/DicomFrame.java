package org.nrg.xapi.model.dicomweb;

import java.io.IOException;
import java.io.OutputStream;

public class DicomFrame {
    private DicomImageObject dicomObject;
    private int frameNumber;

    public DicomFrame(DicomImageObject dicomObject, int frameNumber) {
        this.dicomObject = dicomObject;
        this.frameNumber = frameNumber;
    }

    public DicomImageObject getDicomObject() {
        return dicomObject;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public byte[] getPixels() throws IOException {
        return dicomObject.getPixelsForFrame( frameNumber);
    }

    public int getPixelDataLength() throws IOException {
        return dicomObject.getPixelDataLength();
    }

    public void writePixelData( OutputStream os) throws IOException {
        dicomObject.writePixelData( frameNumber, os);
    }
}
