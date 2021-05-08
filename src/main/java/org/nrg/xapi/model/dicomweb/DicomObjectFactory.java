package org.nrg.xapi.model.dicomweb;

import org.nrg.xapi.model.dicomweb.dcm4che3.DicomObjectChe3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created by davidmaffitt on 7/9/17.
 */
@Component
public class DicomObjectFactory {
    private static FrameGrabber frameGrabber;

    @Autowired
    public DicomObjectFactory( FrameGrabber frameGrabber) {
        this.frameGrabber = frameGrabber;
    }

    public static DicomObjectI create(File f, boolean isTemporary) throws IOException {
        return new DicomObjectChe3(f, isTemporary, frameGrabber);
    }

    public static DicomObjectI createQuiet(File f, boolean isTemporary) {
        try {
            return new DicomObjectChe3(f, isTemporary, frameGrabber);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
