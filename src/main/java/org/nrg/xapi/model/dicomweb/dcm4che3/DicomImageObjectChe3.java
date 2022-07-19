package org.nrg.xapi.model.dicomweb.dcm4che3;

//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dcm4che3.data.*;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.json.JSONWriter;
import org.dcm4che3.util.StreamUtils;
import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.FrameGrabber;
import org.nrg.xapi.rest.dicomweb.JsonDicomObjectSerializer;

import javax.xml.transform.sax.TransformerHandler;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * References an object on disk. Delete the object on disk if it is labeled as temporary.
 *
 */
@JsonSerialize(using= JsonDicomObjectSerializer.class)
public class DicomImageObjectChe3 extends DicomObjectChe3 implements DicomImageObject {

    // Attributes are inherited from DicomObjectChe3
    //    protected Attributes attributes;
    private final File file;
    private RandomAccessFile randomAccessFile = null;
    Long[] frameOffsets = null;
    Long[] frameLengths = null;
    DicomInputStream dis = null;
    int currentFrameNumber=1;
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

    public void finalize() throws Throwable {
        if (dis != null) {
            dis.close();
        }
    }
    @Override
    public int getLength() {
        return (int)file.length();
    }
    @Override
    public void writeAsPart10 (OutputStream os) throws IOException {
        this.writeFile(os);
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
        //return new DicomInputStream( file);
        return dis;
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

    @Override
    public void writePixelDataRandomFrame(int frame, OutputStream os) throws IOException {
        Long length = frameLengths[frame-1];
        byte[] pixelData = new byte[length.intValue()];
        randomAccessFile.read(pixelData);
        os.write(pixelData);
    }

    public byte[] getPixels() throws IOException {
        byte[] pixels = getBytes( PIXEL_DATA);
        if( pixels == null || pixels.length == 0) {
            try (DicomInputStream dis = new DicomInputStream( file)) {
                Attributes dataSet = dis.readDataset( -1, -1);
                attributes.setBytes( PIXEL_DATA, dataSet.getVR( PIXEL_DATA), dataSet.getBytes( PIXEL_DATA) );
                pixels = getBytes( PIXEL_DATA);
            }
        }
        int k = pixels.length;
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

    public void seekToFrame(int frameNumber) throws IOException {
        if (randomAccessFile == null) {
            randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");

            List<Long> offsetList = new ArrayList<>();
            List<Long> lengthList = new ArrayList<>();

            File indexFile = new File(file.getAbsolutePath() + ".txt");
            try(BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
                for (String line; (line = br.readLine()) != null; ) {
                    String[] tokens = line.split("\t");
                    Long offset = Long.parseLong(tokens[0]);
                    Long length = Long.parseLong(tokens[1]);
                    offsetList.add(offset);
                    lengthList.add(length);
                }
            }
            frameOffsets = offsetList.toArray(new Long[0]);
            frameLengths = lengthList.toArray(new Long[0]);
        }
        long position = frameOffsets[frameNumber-1];
        randomAccessFile.seek(position);
        /*
        if (dis == null) {
            dis = new DicomInputStream(file);
            int j = dis.length();
            dis.readDataset(-1, Tag.PixelData);
            int k = dis.length();
            if (dis.tag() != Tag.PixelData || dis.length() != -1 || !dis.readItemHeader()) {
                throw new IOException("No or incorrect encapsulated compressed pixel data in requested object");
            }
            dis.skipFully(dis.length());
            int l = dis.length();
        }

        int m = dis.length();

        while (currentFrameNumber < frameNumber) {
            skipFrame(dis);
            currentFrameNumber++;
        }
        dis.readItemHeader();
        int n = dis.length();

         */
    }

    public int getCurrentFrame() {
        return currentFrameNumber;
    }
    public int getCurrentFrameLength() {
//        return (dis == null) ? 0 : dis.length();
        return (frameLengths == null) ? 0 : Math.toIntExact(frameLengths[currentFrameNumber-1]);
    }


    private void skipFrame(DicomInputStream dis) throws IOException {
        // TODO fix the implementation of temporary files. These should really be cached.
        Path tmp = Paths.get("/tmp/foo");
        Path f = Files.createTempFile(tmp, null, null);
        OutputStream o = Files.newOutputStream(f);
        dis.readItemHeader();
        int j = dis.length();
        StreamUtils.copy(dis, o, j);
        int k = dis.length();
        String p = f.toString();
        String x = p;
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
    public String getStudyInstanceUID() { return attributes.getString( 0x0020000D); }

    @Override
    public String getSeriesInstanceUID() { return attributes.getString( 0x0020000E); }

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
