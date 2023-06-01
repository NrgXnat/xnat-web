package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class Dicom2JsonMessageConverter extends AbstractHttpMessageConverter<DicomObject> {

    public Dicom2JsonMessageConverter() {
        super(new MediaType("application","dicom+json"));
    }

    @Override
    protected DicomObject readInternal(Class<? extends DicomObject> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return DicomObject.class.isAssignableFrom( clazz);
    }

    @Override
    protected void writeInternal(DicomObject dicomObject, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            dicomObject.writeAsJSON( httpOutputMessage.getBody());

        } catch (IOException e) {
            throw new HttpMessageNotWritableException("Error writing dicom object as JSON.", e);
        }
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        boolean canWrite = super.canWrite(clazz, mediaType);
        return canWrite;
    }
}
