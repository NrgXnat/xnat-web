package org.nrg.xapi.model.dicomweb.framegrabber.basic;

import org.dcm4che3.data.Tag;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.FrameGrabber;

import java.io.IOException;
import java.util.Arrays;

public class BasicFrameGrabber implements FrameGrabber {

    /**
     * Assumes the pixel data is uncompressed EVLE.
     *
     * @param frameNumber The frame to grab, counting from 1.
     * @return byte array of uncompressed EVLE image data
     * @throws IOException
     */
    public byte[] getPixelsForFrame( DicomObjectI dicomObject, int frameNumber) throws IOException {
        byte[] pixels = dicomObject.getPixels();
        byte[] framePixels = null;
        if( pixels != null) {
            int rows = dicomObject.getRows();
            int columns = dicomObject.getColumns();
            int samplePerPixel = dicomObject.getInt(Tag.SamplesPerPixel, 1);
            int bitsAllocated = dicomObject.getInt(Tag.BitsAllocated, 8);
            int frameSizeInBytes = rows * columns * samplePerPixel * bitsAllocated / 8;
            int from = (frameNumber - 1) * frameSizeInBytes;
            int to = from + frameSizeInBytes;
            framePixels = Arrays.copyOfRange( pixels, from, to);
        }
        return framePixels;
    }

}
