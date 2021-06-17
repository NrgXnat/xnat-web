package org.nrg.xapi.model.dicomweb.dcm4che3;

import org.dcm4che2.data.UID;
import org.dcm4che3.imageio.codec.TransferSyntaxType;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.TransCoder;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransCoderChe3 implements TransCoder {

    Dcm2Dcm dcm2Dcm;

    public TransCoderChe3() {
        this.dcm2Dcm = new Dcm2Dcm();
    }

    @Override
    public void transcode(DicomObjectI inDcm, String dstTsuid, OutputStream os) throws TransCoderException {
        String srcTsuid = inDcm.getTransferSyntaxUID();
        try {
            if ( srcTsuid != null && srcTsuid.equals(dstTsuid)) {
                inDcm.write(os);
            }
            else {
                dcm2Dcm.setTransferSyntax( dstTsuid);

                dcm2Dcm.transcode( new DicomInputStream( inDcm.getFile()), new DicomOutputStream(os, dstTsuid));
            }
        }
        catch( Exception e) {
            String msg = MessageFormat.format("Error transcoding DICOM object from transferSyntax {0} to {1}", srcTsuid, dstTsuid);
            throw new TransCoderException(msg, e);
        }
    }

    @Override
    public DicomObjectI transcode(DicomObjectI inDcm, String dstTsuid) throws TransCoderException {
        String srcTsuid = inDcm.getTransferSyntaxUID();
        try {
            if ( srcTsuid != null && srcTsuid.equals(dstTsuid)) {
                return inDcm;
            }
            else {
                Path tmpFile = Files.createTempFile( "dw","dcm");
                try( OutputStream os =  new FileOutputStream( tmpFile.toFile())) {
                    transcode( inDcm, dstTsuid, os);
                }
                DicomObjectI dobj = DicomObjectFactory.create( tmpFile.toFile(), true);
                return dobj;
            }
        } catch (IOException e) {
            throw new TransCoderException( e);
        }
    }

    @Override
    public boolean isSupportedTransferSyntax(String tranferSyntaxUID) {
        return dcm2Dcm.isSupportedTransferSyntax( tranferSyntaxUID);
    }

    /**
     * what compressed encodings can be generated from the given encoding?
     * TODO: make this real.
     *
     * @param transferSyntaxUID
     * @return
     */
    @Override
    public List<String> getAcceptableTranscodings( String transferSyntaxUID) {
        List<String> tsuids = new ArrayList<>();
        switch (transferSyntaxUID) {
            default:
                tsuids.add( JPG_BASELINE);
        }
        return tsuids;
    }

    private final String EVLE = "1.2.840.10008.1.2.1";
    private final String JPG_LOSSLESS = "1.2.840.10008.1.2.4.70";
    private final String JPG_BASELINE = "1.2.840.10008.1.2.4.50";
    private final String JPG_BASELINE_12 = "1.2.840.10008.1.2.4.51";
    private final String JPG_LOSSLESS_P14 = "1.2.840.10008.1.2.4.57";
    private final String RLE = "1.2.840.10008.1.2.5";
    private final String JPG_LS_LOSSLESS = "1.2.840.10008.1.2.4.80";
    private final String JPG_LS_LOSSY = "1.2.840.10008.1.2.4.81";
    private final String JPG_2000_LOSSLESS = "1.2.840.10008.1.2.4.90";
    private final String JPG_2000 = "1.2.840.10008.1.2.4.91";
    private final String JPG_2000_LOSSLESS_MULTI = "1.2.840.10008.1.2.4.92";
    private final String JPG_2000_MULTI = "1.2.840.10008.1.2.4.93";

}
