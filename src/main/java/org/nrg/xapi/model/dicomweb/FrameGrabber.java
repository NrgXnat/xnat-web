package org.nrg.xapi.model.dicomweb;

import java.io.IOException;

public interface FrameGrabber {

    byte[] getPixelsForFrame(DicomImageObject dicomObject, int frameNumber) throws IOException;
}
