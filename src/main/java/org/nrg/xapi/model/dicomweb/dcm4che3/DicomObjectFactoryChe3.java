package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.nrg.xapi.model.dicomweb.*;

import java.io.File;
import java.io.IOException;

public class DicomObjectFactoryChe3 implements DicomObjectFactory {
    private static FrameGrabber frameGrabber;

    public DicomObjectFactoryChe3(FrameGrabber frameGrabber) {
        this.frameGrabber = frameGrabber;
    }

    public DicomImageObject createDicomObject(File f, boolean isTemporary) throws IOException {
        return new DicomImageObjectChe3(f, isTemporary, frameGrabber);
    }

    public DicomImageObject createDicomObjectQuiet(File f, boolean isTemporary) {
        try {
            return new DicomImageObjectChe3(f, isTemporary, frameGrabber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QIDOResponseStudy createQIDOResponseStudy() {
        return new QIDOResponseStudyChe3();
    }

    @Override
    public QIDOResponseSeries createQIDOResponseSeries() {
        return new QIDOResponseSeriesChe3();
    }

    @Override
    public QIDOResponseStudySeries createQIDOResponseStudySeries() {
        return new QIDOResponseStudySeriesChe3();
    }

    @Override
    public QIDOResponseInstance createQIDOResponseInstance() {
        return new QIDOResponseInstanceChe3();
    }
}
