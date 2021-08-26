package org.nrg.xapi.model.dicomweb.dcm4che3;

//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dcm4che3.data.*;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.json.JSONWriter;
import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.FrameGrabber;
import org.nrg.xapi.rest.dicomweb.JsonDicomObjectSerializer;

import javax.xml.transform.sax.TransformerHandler;
import java.io.*;

/**
 * References an object on disk. Delete the object on disk if it is labeled as temporary.
 *
 */
@JsonSerialize(using= JsonDicomObjectSerializer.class)
public class DicomImageObjectChe3 extends DicomObjectChe3 implements DicomImageObject {

    // Attributes are inherited from DicomObjectChe3
    //    protected Attributes attributes;
    private final File file;
    private boolean isTemporary;
    private final static int PIXEL_DATA = 0x7FE00010;
    private TransformerHandler transformerHandler;

    private final FrameGrabber frameGrabber;

    public DicomImageObjectChe3(File file, boolean isTemporary, FrameGrabber frameGrabber) throws IOException {
        this.file = file;
        this.isTemporary = isTemporary;
        readAll();
        this.frameGrabber = frameGrabber;
        // Create the handler only if it is needed.
        this.transformerHandler = null;
    }

    /**
     * Blast the file, as is, down stream.
     *
     * @param os
     * @throws IOException
     */
    public void writeFile(OutputStream os) throws IOException {
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
    public int getPixelDataLength() throws IOException {
        int length = 0;
        Object o = attributes.getValue( PIXEL_DATA);
        if( o != null) {
            if( o instanceof byte[] ) {
                length = ((byte[]) o).length;
            }
            else if( o instanceof Fragments) {
                Fragments f = (Fragments) o;
                if( ! f.isEmpty()) {
                    for( int i = 1; i < f.size(); i++) {
                        length += ((byte[]) f.get(i)).length;
                    }
                }
            }
        }
        return length;
    }

    @Override
    public void writePixelData(OutputStream os) throws IOException {
        Object o = attributes.getValue( PIXEL_DATA);
        if( o != null) {
            if( o instanceof byte[] ) {
                os.write((byte[]) o);
            }
            else if( o instanceof Fragments) {
                Fragments f = (Fragments) o;
                if( ! f.isEmpty()) {
                    for( int i = 1; i < f.size(); i++) {
                        os.write((byte[]) f.get(i));
                    }
                }
            }
        }
    }

    @Override
    public int getPixelDataLength( int frame) throws IOException {
        byte[] pixelData = frameGrabber.getPixelsForFrame( this, frame);
        return pixelData.length;
    }

    @Override
    public void writePixelData(int frame, OutputStream os) throws IOException {
        byte[] pixelData = frameGrabber.getPixelsForFrame( this, frame);
        os.write( pixelData);
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

    protected final int[] skipTags = { PIXEL_DATA};

    @Override
    public void writeAsJSON(javax.json.stream.JsonGenerator jsonGenerator) throws IOException {
        JSONWriter jsonWriter = new JSONWriter( jsonGenerator);
        Attributes tmpAttributes = new Attributes();
        tmpAttributes.addNotSelected( attributes, skipTags);

        jsonWriter.write( tmpAttributes);
        jsonGenerator.flush();
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

    @Override
    public String getModality() { return attributes.getString( 0x00080060); }

}
