package org.nrg.xapi.model.dicomweb;

public class DicomFrame {
    private DicomObjectI dicomObject;
    private int frameNumber;

    public DicomFrame( DicomObjectI dicomObject, int frameNumber) {
        this.dicomObject = dicomObject;
        this.frameNumber = frameNumber;
    }

    public DicomObjectI getDicomObject() {
        return dicomObject;
    }

    public int getFrameNumber() {
        return frameNumber;
    }
}
