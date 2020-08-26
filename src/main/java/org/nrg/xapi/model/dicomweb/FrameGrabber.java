package org.nrg.xapi.model.dicomweb;

import java.io.IOException;

public interface FrameGrabber {

    byte[] getPixelsForFrame( DicomObjectI dicomObject, int frameNumber) throws IOException;
}
