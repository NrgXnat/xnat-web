package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.DatasetWithFMI;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputHandler;
import org.dcm4che3.io.DicomInputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectI;

import java.io.*;

public class DicomObjectChe3 implements DicomObjectI{

    private final File file;
    private Attributes attributes = null;

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

    private void readHeader() throws IOException {
        DicomInputStream dis = new DicomInputStream( file);
//        attributes = dis.readDataset(-1, Tag.PixelData);
        DatasetWithFMI data  = dis.readDatasetWithFMI(-1, Tag.PixelData);

        attributes = data.getFileMetaInformation();
        attributes.addAll( data.getDataset());
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
