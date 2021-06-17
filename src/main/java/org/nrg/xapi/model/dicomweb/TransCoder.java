package org.nrg.xapi.model.dicomweb;

import java.io.OutputStream;
import java.util.List;

public interface TransCoder {

    void transcode( DicomObjectI dcmObj, String transferSyntaxUID, OutputStream os) throws TransCoderException;

    DicomObjectI transcode( DicomObjectI dcmObj, String transferSyntaxUID) throws TransCoderException;

    boolean isSupportedTransferSyntax( String tranferSyntaxUID);

    List<String> getAcceptableTranscodings( String transferSyntaxUID);
}
