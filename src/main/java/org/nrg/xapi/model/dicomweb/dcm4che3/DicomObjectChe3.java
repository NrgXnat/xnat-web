package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che3.data.*;
import org.dcm4che3.io.DicomEncodingOptions;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.FrameGrabber;

import java.io.*;

/**
 * References an object on disk. Delete the object on disk if it is labeled as temporary.
 *
 */
public class DicomObjectChe3 implements DicomObjectI{

    private final File file;
    private boolean isTemporary;
    private Attributes attributes = null;
    private static int PIXEL_DATA = 0x7FE00010;

    private final FrameGrabber frameGrabber;

    public DicomObjectChe3( File file, boolean isTemporary, FrameGrabber frameGrabber) throws IOException {
        this.file = file;
        this.isTemporary = isTemporary;
        readAll();
        this.frameGrabber = frameGrabber;
    }

    // TODO: leak attributes which is bad.  clean this up. Used as quick fix for json serializing these objects.
    public Attributes getAttributes() {
        return attributes;
    }

    public int getLength() {
        attributes.calcLength( DicomEncodingOptions.DEFAULT, true);
        return attributes.getLength();
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
            if( this.isTemporary) {
                file.delete();
                attributes = null;
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
    public int getInt( int tag, int def) {
        return attributes.getInt( tag, def);
    }

    @Override
    public byte[] getBytes( int tag) throws IOException {
        return attributes.getBytes(tag);
    }

    public Value readPixels() throws IOException {
        Value v = (Value) attributes.getValue( PIXEL_DATA);
        if( v == null || v.isEmpty()) {
            try (DicomInputStream dis = new DicomInputStream(file)) {
                Attributes dataSet = dis.readDataset(-1, -1);
                v = (Value) dataSet.getValue(PIXEL_DATA);
                if (v != null) {
                    attributes.setValue(PIXEL_DATA, dataSet.getVR(PIXEL_DATA), v);
                }
            }
        }
        return v;
    }

    @Override
    public int getPixelDataLength() throws IOException {
        Value v = readPixels();
        int length = 0;
        VR vr = attributes.getVR( PIXEL_DATA);
        if( v instanceof Fragments) {
            // Compressed Bulkdata contains only the compressed octet stream without the fragment delimiters.
            Fragments f = (Fragments) v;
            for( int i = 1; i < f.size(); i++) {
                length += ((byte[]) f.get(i)).length;
            }
        }
        else {
            length = v.toBytes( vr, false).length;
        }
        return length;
    }

    @Override
    public void writePixelData(OutputStream os) throws IOException {
        Value v = readPixels();
        VR vr = attributes.getVR( PIXEL_DATA);
        if( v instanceof Fragments) {
            // Compressed Bulkdata contains only the compressed octet stream without the fragment delimiters.
            Fragments f = (Fragments) v;
            for( int i = 1; i < f.size(); i++) {
                os.write( (byte[]) f.get(i));
            }
        }
        else {
            os.write( v.toBytes( vr, false));
        }
    }

    private void writePixelData( Value v, VR vr, OutputStream os) throws IOException {
        if( ! (v == null || v.isEmpty())) {
            if (v instanceof Fragments) {
                // Compressed Bulkdata contains only the compressed octet stream without the fragment delimiters.
                Fragments f = (Fragments) v;
                for (int i = 1; i < f.size(); i++) {
                    os.write((byte[]) f.get(i));
                }
            } else {
                os.write(v.toBytes(vr, false));
            }
        }
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
     * Delegate to the FrameGrabber.
     *
     * @param frameNumber The frame to grab, counting from 1.
     * @return byte array of uncompressed EVLE image data
     * @throws IOException
     */
    public byte[] getPixelsForFrame( int frameNumber) throws IOException {
        return frameGrabber.getPixelsForFrame( this, frameNumber);
    }

//    private void readHeader() throws IOException {
//        DicomInputStream dis = new DicomInputStream( file);
//        attributes = dis.getFileMetaInformation();
//        attributes.addAll( dis.readDataset( -1, Tag.PixelData));
//    }

    private void readAll() throws IOException {
        DicomInputStream dis = new DicomInputStream( file);
        attributes = dis.getFileMetaInformation();
        attributes.addAll( dis.readDataset( -1, -1));
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
