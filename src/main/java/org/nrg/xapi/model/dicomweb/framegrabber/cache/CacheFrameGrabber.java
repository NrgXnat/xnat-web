package org.nrg.xapi.model.dicomweb.framegrabber.cache;

import org.dcm4che3.data.Tag;
import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.FrameGrabber;
import org.nrg.xapi.model.dicomweb.TransCoder;

import java.io.IOException;
import java.util.Arrays;

public class CacheFrameGrabber implements FrameGrabber {

    private DicomObjectCache dicomObjectCache;

    public CacheFrameGrabber( TransCoder transCoder) {
        this.dicomObjectCache = new DicomObjectCache( transCoder);
    }

    /**
     * Assumes the pixel data is uncompressed EVLE.
     *
     * @param frameNumber The frame to grab, counting from 1.
     * @return byte array of uncompressed EVLE image data
     * @throws IOException
     */
    public byte[] getPixelsForFrame(DicomImageObject dicomObject, int frameNumber) throws IOException {
        DicomImageObject dobj = dicomObjectCache.getDicomObject( dicomObject);
        byte[] pixels = dobj.getPixels();
        byte[] framePixels = null;
        if( pixels != null) {
            int rows = dobj.getRows();
            int columns = dobj.getColumns();
            int samplePerPixel = dobj.getInt(Tag.SamplesPerPixel, 1);
            int bitsAllocated = dobj.getInt(Tag.BitsStored, 8);
            int frameSizeInBytes = rows * columns * samplePerPixel * bitsAllocated / 8;
            int from = (frameNumber - 1) * frameSizeInBytes;
            int to = from + frameSizeInBytes;
            framePixels = Arrays.copyOfRange( pixels, from, to);
        }
        return framePixels;
    }

}
