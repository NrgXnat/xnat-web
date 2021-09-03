package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.DicomImageObjects;
import org.nrg.xapi.model.dicomweb.TransCoder;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.servlet.HandlerMapping;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MultipartCompressedDicomFileMessageConverter extends AbstractHttpMessageConverter<DicomImageObjects> {

    @Autowired
    HttpServletRequest request;
    @Autowired
    SiteConfigPreferences preferences;
    @Autowired
    private TransCoder transCoder;

    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    private final static MediaType APPLICATION_OCTETSTREAM = new MediaType("application", "octet-stream");
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
    protected DicomImageObjects readInternal(Class<? extends DicomImageObjects> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(DicomImageObjects dicomImageObjects, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {

        try {
            if( dicomImageObjects.isEmpty()) {
                String msg = "Error. Attempting to write response with no body.";
                _log.error(msg);
                throw new HttpMessageNotWritableException(msg);
            }
            DicomImageObject dobj = dicomImageObjects.get(0);

            String inputTsuid = dobj.getTransferSyntaxUID();
            final String tsuid = getAcceptableTransferSyntax( inputTsuid).orElseThrow( () -> {
                String msg = String.format("Error finding acceptable transfer syntax for data in: %s", inputTsuid);
                _log.error(msg);
                return new HttpMessageNotWritableException(msg);
            });

            MimeType partContentType = getContentType( tsuid).orElseThrow( () -> {
                String msg = String.format("Error finding part media type for transfer syntax uid: %s", tsuid);
                _log.error(msg);
                return new HttpMessageNotWritableException(msg);
            });

            HttpHeaders outputHeaders = outputMessage.getHeaders();
            Map<String, String> contentTypeArgs = new HashMap<>(1);
            String boundary = getBoundary();
            contentTypeArgs.put("type", "\"" + partContentType.toString() + "\"");
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType("multipart", "related", contentTypeArgs);
            outputHeaders.setContentType( mediaType);

            int frameNumber = getFrameNumber(request);
            String contentLocation = getContentLocation(request);

            // write preamble, just CRLF if preamble is empty.
            // DICOM Part 18 seems to ignore this.
            // outputMessage.getBody().write( "\r\n".getBytes());

            for (DicomImageObject dicomImageObject : dicomImageObjects) {

                DicomImageObject dcmOut = transCoder.transcode(dicomImageObject, tsuid);

                outputMessage.getBody().write(("--" + boundary + "\r\n").getBytes());
                outputMessage.getBody().write(("Content-Location: " + contentLocation + "\r\n").getBytes());
                outputMessage.getBody().write(("Content-Type: " + partContentType + "\r\n").getBytes());
////                outputMessage.getBody().write(("Content-Length: " + dcmOut.getLength() + "\r\n\r\n").getBytes());
////                dcmOut.write(outputMessage.getBody());
//                outputMessage.getBody().write(("Content-Length: " + dcmOut.getPixelDataLength() + "\r\n\r\n").getBytes());
//                dcmOut.writePixelData( outputMessage.getBody());
                outputMessage.getBody().write(("Content-Length: " + dcmOut.getPixelDataLength( frameNumber) + "\r\n\r\n").getBytes());
                dcmOut.writePixelData( frameNumber, outputMessage.getBody());

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
        return DicomImageObjects.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        boolean canWrite = false;
        if( supports( clazz) && MULTIPART_RELATED.isCompatibleWith( mediaType)) {
            MediaType partMediaType = getPartType( mediaType);
            String tx = getTransferSyntax( mediaType);
            canWrite = canWrite( partMediaType, tx);
        }
        return canWrite;
    }

    private boolean canWrite(MediaType partMediaType, String tsuid) {
        boolean canWrite = false;
        if( APPLICATION_OCTETSTREAM.isCompatibleWith( partMediaType)) {
            switch (tsuid) {
                case "1.2.840.10008.1.2.1":
                    canWrite = true;
            }
        }
        else if( IMAGE_JPG.isCompatibleWith( partMediaType)) {
            switch (tsuid) {
                case "1.2.840.10008.1.2.4.70":
                case "1.2.840.10008.1.2.4.50":
                case "1.2.840.10008.1.2.4.51":
                    canWrite = true;
            }
        }
        else if( IMAGE_JLS.isCompatibleWith( partMediaType)) {
            switch (tsuid) {
                case "1.2.840.10008.1.2.4.80":
                case "1.2.840.10008.1.2.4.81":
                    canWrite = true;
            }
        }
        else if( IMAGE_JP2.isCompatibleWith( partMediaType)) {
            switch (tsuid) {
                case "1.2.840.10008.1.2.4.90":
                case "1.2.840.10008.1.2.4.91":
                    canWrite = true;
            }
        }
        else if( IMAGE_JPX.isCompatibleWith( partMediaType)) {
            switch (tsuid) {
                case "1.2.840.10008.1.2.4.92":
                case "1.2.840.10008.1.2.4.93":
                    canWrite = true;
            }
        }
        return canWrite;
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

    private Optional<String> getAcceptableTransferSyntax( String inputTsuid) {
        if( isAcceptedTransferSyntax( inputTsuid)) {
            return Optional.of( inputTsuid);
        }
        else {
            return transCoder.getAcceptableTranscodings( inputTsuid).stream()
                    .filter( getAcceptedTransferSyntaxes()::contains)
                    .findAny();
        }
    }

    /**
     * Map from transfer-syntax uid to the corresponding Mime type.
     *
     * @param transferSyntax
     * @return
     */
    private Optional<MimeType> getContentType( String transferSyntax) {
        MimeType mt = new MimeType();
        try {
            switch (transferSyntax) {
                case "1.2.840.10008.1.2.1":
                    mt.setPrimaryType("application");
                    mt.setSubType("dicom");
                    mt.setParameter("transfer-syntax", transferSyntax);
                    break;
                case "1.2.840.10008.1.2.4.70":
                case "1.2.840.10008.1.2.4.50":
                case "1.2.840.10008.1.2.4.51":
                    mt.setPrimaryType("image");
                    mt.setSubType("jpeg");
                    mt.setParameter("transfer-syntax", transferSyntax);
                    break;
                case "1.2.840.10008.1.2.4.80":
                case "1.2.840.10008.1.2.4.81":
                    mt.setPrimaryType("image");
                    mt.setSubType("jls");
                    mt.setParameter("transfer-syntax", transferSyntax);
                    break;
                case "1.2.840.10008.1.2.4.90":
                case "1.2.840.10008.1.2.4.91":
                    mt.setPrimaryType("image");
                    mt.setSubType("jp2");
                    mt.setParameter("transfer-syntax", transferSyntax);
                    break;
                case "1.2.840.10008.1.2.4.92":
                case "1.2.840.10008.1.2.4.93":
                    mt.setPrimaryType("image");
                    mt.setSubType("jpx");
                    mt.setParameter("transfer-syntax", transferSyntax);
                    break;
                default:
                    mt = null;
            }
        }
        catch (MimeTypeParseException e) {
            // ignore. There should not be any parse error for hardcoded values.
        }
        return Optional.ofNullable( mt);
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
        if( APPLICATION_OCTETSTREAM.isCompatibleWith( mediaType)) {
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

    /**
     * Search the request's list of acceptable transfer syntaxes for a match.
     *
     * @param tsuid
     * @return
     */
    private boolean isAcceptedTransferSyntax( String tsuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Accept", request.getHeader("Accept"));
        List<MediaType> acceptedMediaTypes = headers.getAccept();

        return acceptedMediaTypes.stream()
                .anyMatch(mt -> tsuid.equals( getTransferSyntax(mt)));
    }

    private Set<String> getAcceptedTransferSyntaxes() {
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Accept", request.getHeader("Accept"));
        List<MediaType> acceptedMediaTypes = headers.getAccept();

        return acceptedMediaTypes.stream()
                .map(mt -> mt.getParameter("transfer-syntax"))
                .collect(Collectors.toSet());
    }

    private  String getBoundary() {
        return String.format( "Part__%s.%s", (new Object()).hashCode(), System.currentTimeMillis());
    }

}