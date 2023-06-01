package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomFrame;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
@Lazy
public class DicomFrameMessageConverter extends AbstractHttpMessageConverter< DicomFrame> {

    @Autowired
    HttpServletRequest request;
    @Autowired
    SiteConfigPreferences preferences;

//    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    private final static MediaType APPLICATION_OCTETSTREAM = new MediaType("application", "octet-stream");
//    private final static MediaType IMAGE_JPG = new MediaType("image", "jpeg");
//    private final static MediaType IMAGE_JLS = new MediaType("image", "jls");
//    private final static MediaType IMAGE_JP2 = new MediaType("image", "jp2");
//    private final static MediaType IMAGE_JPX = new MediaType("image", "jpx");
    private static final Logger _log = LoggerFactory.getLogger(DicomFrameMessageConverter.class);

    public DicomFrameMessageConverter() {
        super( APPLICATION_OCTETSTREAM);
    }

    // for reading from the input message.
    @Override
    protected DicomFrame readInternal(Class<? extends DicomFrame> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal( DicomFrame frame, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {

        try {

            frame.getDicomObject().writePixelData( frame.getFrameNumber(), outputMessage.getBody());

        } catch (IOException | TransCoderException e) {
            String msg = "Error streaming dicom: " + e.getMessage();
            _log.error(msg);
            throw new HttpMessageNotWritableException(msg, e.getCause());
        }
    }

    private String getContentLocation(HttpServletRequest request) {
        String host = preferences.getSiteUrl();
        return host + request.getRequestURI();
    }

    private int getFrameNumber(HttpServletRequest request) {
        final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String s = pathVariables.get("frameNumber");
        return (s != null) ? Integer.parseInt(s) : 1;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return DicomFrame.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        boolean b = supports( clazz) && canWrite( mediaType);
        return supports( clazz) && canWrite( mediaType);
    }

}
