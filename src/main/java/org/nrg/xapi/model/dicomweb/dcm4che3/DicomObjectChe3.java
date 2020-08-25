package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectI;

import java.io.*;
import java.util.Arrays;

public class DicomObjectChe3 implements DicomObjectI{

    private final File file;
    private Attributes attributes = null;
    private static int PIXEL_DATA = 0x7FE00010;

    public DicomObjectChe3(File file) throws IOException {
        this.file = file;
        readHeader();
    }

    // TODO: leak attributes which is bad.  clean this up. Used as quick fix for json serializing these objects.
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Blast the file, as is, down stream.
     *
     * @param os
     * @throws IOException
     */
    @Override
    public void write(OutputStream os) throws IOException {
        byte[] buf = new byte[16384];
        try ( InputStream is = new FileInputStream(file)) {
            int bytes;
            while( (bytes = is.read(buf)) != -1) {
                os.write(buf, 0, bytes);
            }
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new DicomInputStream( file);
    }

    @Override
    public File getFile() {
        return file;
    }

    /**
     * Return the transfer syntax uid or null if it is missing or IO error.
     *
     * @return
     */
    @Override
    public String getTransferSyntaxUID() {
        return getString(Tag.TransferSyntaxUID);
    }

    @Override
    public String getString( int tag) {
        return attributes.getString( tag);
    }

    @Override
    public byte[] getBytes( int tag) throws IOException {
        return attributes.getBytes( tag);
    }

    public byte[] getPixels() throws IOException {
        byte[] pixels = getBytes( PIXEL_DATA);
        if( pixels == null) {
            try (DicomInputStream dis = new DicomInputStream( file)) {
                Attributes dataSet = dis.readDataset( -1, -1);
                attributes.setBytes( PIXEL_DATA, dataSet.getVR( PIXEL_DATA), dataSet.getBytes( PIXEL_DATA) );
                pixels = getBytes( PIXEL_DATA);
            }
        }
        return pixels;
    }

    /**
     * Assumes the pixel data is uncompressed EVLE.
     *
     * @param frameNumber The frame to grab, counting from 1.
     * @return byte array of uncompressed EVLE image data
     * @throws IOException
     */
    public byte[] getPixelsForFrame( int frameNumber) throws IOException {
        byte[] pixels = getPixels();
        byte[] framePixels = null;
        if( pixels != null) {
            int rows = getRows();
            int columns = getColumns();
            int samplePerPixel = attributes.getInt(Tag.SamplesPerPixel, 1);
            int bitsAllocated = attributes.getInt(Tag.BitsAllocated, 8);
            int frameSizeInBytes = rows * columns * samplePerPixel * bitsAllocated / 8;
            int from = (frameNumber - 1) * frameSizeInBytes;
            int to = from + frameSizeInBytes;
            framePixels = Arrays.copyOfRange( pixels, from, to);
        }
        return framePixels;
    }

    private void readHeader() throws IOException {
        DicomInputStream dis = new DicomInputStream( file);
        attributes = dis.getFileMetaInformation();
        attributes.addAll( dis.readDataset( -1, Tag.PixelData));
    }

    @Override
    public String getStudyInstanceUID() { return attributes.getString( 0x002000D); }

    @Override
    public String getSeriesInstanceUID() { return attributes.getString( 0x002000E); }

    @Override
    public String getSOPInstanceUID() { return attributes.getString( 0x00080018); }

    @Override
    public String getSOPClassUID() { return attributes.getString( 0x00080016); }

    @Override
    public Integer getInstanceNumber() {
        String s = attributes.getString( 0x00200013);
        return (s != null)? Integer.valueOf(s): null;
    }

    @Override
    public Integer getRows() {
        String s = attributes.getString( 0x00280010);
        return (s != null)? Integer.valueOf( s): null;
    }

    @Override
    public Integer getColumns() {
        String s = attributes.getString( 0x00280011);
        return (s != null)? Integer.valueOf( s): null;
    }

    @Override
    public Integer getBitsAllocated() {
        String s = attributes.getString( 0x00280100);
        return (s != null)? Integer.valueOf( s): null;
    }

    @Override
    public Integer getNumberOfFrames() {
        String s = attributes.getString( 0x00280008);
        return (s != null)? Integer.valueOf( s): 1;
    }

    @Override
    public Integer getFrameNumber() { return null; }

    @Override
    public String getImagePositionPatient() { return attributes.getString( 0x00200032); }

    @Override
    public String getImageOrientationPatient() { return attributes.getString( 0x00200037); }

    @Override
    public String getPixelSpacing() { return attributes.getString( 0x00280030); }

    @Override
    public String getFrameOfReferenceUid() { return attributes.getString( 0x00200052); }
}
