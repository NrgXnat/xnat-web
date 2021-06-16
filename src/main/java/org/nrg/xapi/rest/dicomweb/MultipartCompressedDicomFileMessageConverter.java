package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.TransCoder;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Lazy
public class MultipartCompressedDicomFileMessageConverter extends AbstractHttpMessageConverter< List<DicomObjectI>> {

    @Autowired
    HttpServletRequest request;
    @Autowired
    SiteConfigPreferences preferences;
    @Autowired
    private TransCoder transCoder;

    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    private final static MediaType APPLICATION_DICOM = new MediaType("application", "dicom");
    private final static MediaType IMAGE_JPG = new MediaType("image", "jpeg");
    private final static MediaType IMAGE_JLS = new MediaType("image", "jls");
    private final static MediaType IMAGE_JP2 = new MediaType("image", "jp2");
    private final static MediaType IMAGE_JPX = new MediaType("image", "jpx");
    private static final Logger _log = LoggerFactory.getLogger(MultipartCompressedDicomFileMessageConverter.class);

    public MultipartCompressedDicomFileMessageConverter() {
        super( MULTIPART_RELATED);
    }

    // for reading from the input message.
    @Override
    protected List<DicomObjectI> readInternal(Class<? extends List<DicomObjectI>> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(List<DicomObjectI> dicomParts, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {

        try {
            HttpHeaders defaultHeaders = outputMessage.getHeaders();
            MediaType defaultMediaType = MediaType.parseMediaType(defaultHeaders.getFirst("Content-Type"));
            MediaType partMediaType = getPartType(defaultMediaType);

            if (partMediaType == null) {
                String msg = String.format("Error finding root-part media type in multipart content: %s", defaultMediaType);
                _log.error(msg);
                throw new HttpMessageNotWritableException(msg);
            }

            String tsuid = getTransferSyntax( defaultMediaType);
            if( tsuid == null) {
                String msg = "Unsupported Transfer Syntax: " + defaultMediaType;
                _log.error(msg);
                throw new HttpMessageNotWritableException(msg);
            }

            HttpHeaders outputHeaders = outputMessage.getHeaders();
            Map<String, String> contentTypeArgs = new HashMap<>(1);
            String boundary = getBoundary();
            contentTypeArgs.put("type", "\"" + partMediaType.toString() + "\"");
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType("multipart", "related", contentTypeArgs);
            outputHeaders.setContentType(mediaType);

            int frameNumber = getFrameNumber(request);
            String contentLocation = getContentLocation(request);

            // write preamble, just CRLF if preamble is empty.
            // DICOM Part 18 seems to ignore this.
            // outputMessage.getBody().write( "\r\n".getBytes());

            for (DicomObjectI dicomPart : dicomParts) {

                String inputTsuid = dicomPart.getTransferSyntaxUID();
                if( isAcceptedTransferSyntax( inputTsuid)) {
                    tsuid = inputTsuid;
                }

                DicomObjectI dcmOut = transCoder.transcode(dicomPart, tsuid);

                outputMessage.getBody().write(("--" + boundary + "\r\n").getBytes());
                outputMessage.getBody().write(("Content-Location: " + contentLocation + "\r\n").getBytes());
                outputMessage.getBody().write(("Content-Type: application/dicom\r\n").getBytes());
                outputMessage.getBody().write(("Content-Length: " + dcmOut.getLength() + "\r\n\r\n").getBytes());
                dcmOut.write(outputMessage.getBody());

                outputMessage.getBody().write(("\r\n--" + boundary + "--\r\n\r\n").getBytes());

            }

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
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
//        MediaType partMediaType = getPartType(mediaType);
        String tx = getTransferSyntax(mediaType);
        return transCoder.isSupportedTransferSyntax( tx);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return MULTIPART_RELATED.isCompatibleWith(mediaType);
    }

    private MediaType getPartType(MediaType mediaType) {
        MediaType partType = null;
        if (MULTIPART_RELATED.isCompatibleWith(mediaType)) {
            String type = mediaType.getParameter("type");
            if (type != null) {
                type = type.replaceAll("^\"|\"$", "");
                partType = MediaType.parseMediaType(type);
            }
        }
        return partType;
    }

    private String getTransferSyntax( MediaType mediaType) {
        String tx = "";
        if (MULTIPART_RELATED.isCompatibleWith(mediaType)) {
            String t = mediaType.getParameter("transfer-syntax");
            tx = (t != null) ? t : getDefaultTransferSyntax( getPartType( mediaType));
        }
        return tx;
    }

    private String getDefaultTransferSyntax( MediaType mediaType) {
        if( APPLICATION_DICOM.isCompatibleWith( mediaType)) {
            return "1.2.840.10008.1.2.1";
        }
        if( IMAGE_JPG.isCompatibleWith( mediaType)) {
            return "1.2.840.10008.1.2.4.70";
        }
        else if( IMAGE_JLS.isCompatibleWith( mediaType)) {
            return "1.2.840.10008.1.2.4.80";
        }
        else if( IMAGE_JP2.isCompatibleWith( mediaType)) {
            return "1.2.840.10008.1.2.4.90";
        }
        else if( IMAGE_JPX.isCompatibleWith( mediaType)) {
            return "1.2.840.10008.1.2.4.92";
        }
        else {
            return null;
        }
    }

    private boolean isAcceptedTransferSyntax( String tsuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Accept", request.getHeader("Accept"));
        List<MediaType> acceptedMediaTypes = headers.getAccept();

        return acceptedMediaTypes.stream()
                .anyMatch(mt -> tsuid.equals( getTransferSyntax(mt)));
    }

    private  String getBoundary() {
        StringBuffer buf = new StringBuffer(64);
        buf.append("Part_").append('_').append((new Object()).hashCode()).append('.').append(System.currentTimeMillis());
        return buf.toString();
    }

}

