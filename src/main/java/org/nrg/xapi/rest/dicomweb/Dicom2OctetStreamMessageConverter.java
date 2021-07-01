package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.DicomObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;

public class Dicom2OctetStreamMessageConverter extends AbstractHttpMessageConverter<DicomImageObject> {
    private int PIXEL_DATA = 0x7FE00010;

    public Dicom2OctetStreamMessageConverter() {
        super(new MediaType("application","octet-stream"));
    }

    @Override
    protected DicomImageObject readInternal(Class<? extends DicomImageObject> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return DicomObject.class.isAssignableFrom( clazz);
    }

    @Override
    protected void writeInternal(DicomImageObject dicomObject, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        OutputStream os = httpOutputMessage.getBody();
        byte[] pixels = dicomObject.getPixels();
        os.write( ("Content-Location: http://localhost/dicom-web/studies/1.3.12.2.1107.5.2.32.35177.30000006121218324675000000034/series/1.3.12.2.1107.5.2.32.35177.3.2006121409455295707319315.0.0.0/instances/1.3.12.2.1107.5.2.32.35177.3.2006121409514362982419763/frames/1\r\n").getBytes());
        os.write( ("Content-Type: application/octet-stream; transfer-syntax=1.2.840.10008.1.2.1\r\n").getBytes());
        os.write( ("Content-Length: 131072\r\n\r\n").getBytes());
        httpOutputMessage.getBody().write( dicomObject.getPixels());
    }

}
