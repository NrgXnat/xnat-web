package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.DicomObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;

public class DicomImageObjectMessageConverter extends AbstractHttpMessageConverter<DicomImageObject> {

    private TransformerHandler transformerHandler = null;

    public DicomImageObjectMessageConverter() {
        super(new MediaType("application","dicom"));
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

        dicomObject.writeFile( httpOutputMessage.getBody());
    }

}
