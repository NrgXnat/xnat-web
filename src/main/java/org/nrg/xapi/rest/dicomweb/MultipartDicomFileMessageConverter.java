package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomObject;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipartDicomFileMessageConverter extends AbstractHttpMessageConverter< List<DicomObject>> {

    public static final String DEFAULT_DICOM_TSUID = "1.2.840.10008.1.2.1";  // Explicit VR Little Endian.

    @Autowired
    HttpServletRequest request;
    @Autowired
    SiteConfigPreferences preferences;

    private final static Map<String, String> DICOM_XML_TYPE = createMediaTypes();
    private static Map<String, String> createMediaTypes() {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("type", "\"application/dicom\"");
        return Collections.unmodifiableMap(aMap);
    }
    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    private final static MediaType APPLICATION_DICOM_XML = new MediaType("application", "dicom+xml");
    private final static MediaType APPLICATION_DICOM = new MediaType("application", "dicom");
    private static final Logger _log = LoggerFactory.getLogger(MultipartDicomFileMessageConverter.class);

    public MultipartDicomFileMessageConverter() {
        super( MULTIPART_RELATED);
    }

    // for reading from the input message.
    @Override
    protected List<DicomObject> readInternal(Class<? extends List<DicomObject>> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(List<DicomObject> dicomParts, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {

        try {
            if( dicomParts.isEmpty()) {
                String msg = "Error. Attempting to write response with no body.";
                _log.error(msg);
                throw new HttpMessageNotWritableException(msg);
            }

            HttpHeaders outputHeaders = outputMessage.getHeaders();
            MediaType defaultMediaType = MediaType.parseMediaType( outputHeaders.getFirst("Content-Type"));
            MediaType partMediaType = getPartType( defaultMediaType);

            if( partMediaType == null) {
                String msg = String.format("Error finding root-part media type in multipart content: %s", defaultMediaType);
                _log.error( msg);
                throw new HttpMessageNotWritableException( msg);
            }

            Map<String,String> contentTypeArgs = new HashMap<>(1);
            String boundary = getBoundary();
            contentTypeArgs.put("type", "\"" + partMediaType.toString() + "\"");
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType( "multipart", "related", contentTypeArgs );
            outputHeaders.setContentType( mediaType);

            boolean isXML = true;
            String mediaTypeParamType = mediaType.getParameter("type");
            if (mediaTypeParamType != null && mediaTypeParamType.equals("\"application/dicom\"")) {
                isXML = false;
            }

            // write preamble, just CRLF if preamble is empty.
            // DICOM Part 18 seems to ignore this.
            // outputMessage.getBody().write( "\r\n".getBytes());

            String crlf="";

            for ( DicomObject dicomPart: dicomParts) {

                outputMessage.getBody().write( (crlf + "--"+ boundary + "\r\n").getBytes());
                crlf = "\r\n";
                if (isXML) {
                    outputMessage.getBody().write(("Content-Type: application/dicom+xml\r\n").getBytes());
                } else {
                    outputMessage.getBody().write(("Content-Type: application/dicom\r\n").getBytes());
                }
                //  TODO This is wrong. Content length is based on the number of bytes in the image file
                int length = dicomPart.getLength();
                outputMessage.getBody().write( ("Content-Length: " + dicomPart.getLength() + "\r\n\r\n").getBytes());
                if (isXML) {
                    dicomPart.writeAsXML(outputMessage.getBody());
                } else {
                    dicomPart.writeAsPart10(outputMessage.getBody());
                }

//                outputMessage.getBody().write( ("\r\n--"+ boundary + "--\r\n\r\n").getBytes());
            }
            outputMessage.getBody().write( ("\r\n--"+ boundary + "--").getBytes());

        } catch (IOException e) {
            String msg = "Error streaming dicom: " + e.getMessage();
            _log.error( msg);
            throw new HttpMessageNotWritableException( msg, e);
        }
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom( clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        MediaType partMediaType = getPartType( mediaType);
//        return APPLICATION_DICOM_XML.isCompatibleWith( partMediaType);

        //TODO
        // Review to see if this is the right converter for APPLICATION_DICOM
        return (APPLICATION_DICOM_XML.isCompatibleWith( partMediaType) || APPLICATION_DICOM.isCompatibleWith(partMediaType));
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {

        return MULTIPART_RELATED.isCompatibleWith( mediaType);
    }

    private MediaType getPartType( MediaType mediaType) {
        MediaType partType = null;
        if( MULTIPART_RELATED.isCompatibleWith(mediaType)) {
            String type = mediaType.getParameter("type");
            if( type != null) {
                type = type.replaceAll("^\"|\"$", "");
                partType = MediaType.parseMediaType( type);
            }
        }
        return partType;
    }

    private  String getBoundary() {
        StringBuffer buf = new StringBuffer(64);
        buf.append("Part_").append('_').append((new Object()).hashCode()).append('.').append(System.currentTimeMillis());
        return buf.toString();
    }

}

