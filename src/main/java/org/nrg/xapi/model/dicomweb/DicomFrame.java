package org.nrg.xapi.model.dicomweb;

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
}
