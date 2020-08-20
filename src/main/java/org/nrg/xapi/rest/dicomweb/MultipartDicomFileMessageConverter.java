package org.nrg.xapi.rest.dicomweb;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.TransCoder;
import org.nrg.xapi.model.dicomweb.TransCoderException;
import org.nrg.xapi.model.dicomweb.UnsupportedTransferSyntaxException;
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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Lazy
public class MultipartDicomFileMessageConverter extends AbstractHttpMessageConverter< List<DicomObjectI>> {

    public static final String DEFAULT_DICOM_TSUID = "1.2.840.10008.1.2.1";  // Explicit VR Little Endian.

    @Autowired
    HttpServletRequest request;
    @Autowired
    SiteConfigPreferences preferences;

    @Autowired
    private List<HttpMessageConverter<?>> _converters;
    private final static Map<String, String> DICOM_XML_TYPE = createMediaTypes();
    private static Map<String, String> createMediaTypes() {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("type", "\"application/dicom\"");
        return Collections.unmodifiableMap(aMap);
    }
    private final static MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    private final static MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    private final static MediaType APPLICATION_DICOM = new MediaType("application", "dicom");
    private final static MediaType APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");
    private final static MediaType APPLICATION_JPEG = new MediaType("application", "jpeg");
    private final static MediaType APPLICATION_DICOM_XML = new MediaType("application", "dicom+xml");
    private static final Logger _log = LoggerFactory.getLogger(MultipartDicomFileMessageConverter.class);

    @Autowired
    private TransCoder transCoder;

    public MultipartDicomFileMessageConverter() {
        super( MULTIPART_MIXED, MULTIPART_RELATED);
        this._converters = null;
    }

    // for reading from the input message.
    @Override
    protected List<DicomObjectI> readInternal(Class<? extends List<DicomObjectI>> arg0, HttpInputMessage arg1) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal( List<DicomObjectI> dicomParts, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        try {
            HttpHeaders defaultHeaders = outputMessage.getHeaders();
            MediaType defaultMediaType = MediaType.parseMediaType( defaultHeaders.getFirst("Content-Type"));
            MediaType partMediaType = getPartType( defaultMediaType);

            if( partMediaType == null) {
                String msg = String.format("Error finding root-part media type in multipart content: %s", defaultMediaType);
                _log.error( msg);
                throw new IOException(msg);
            }

            String tsuid = defaultMediaType.getParameter("transfer-syntax");
            tsuid = (tsuid == null)? DEFAULT_DICOM_TSUID: tsuid;

            if( ! transCoder.isSupportedTransferSyntax( tsuid)) {
                throw new UnsupportedTransferSyntaxException( tsuid);
            }

            HttpHeaders headers = new HttpHeaders();
            Map<String,String> contentTypeArgs = new HashMap<>(1);
            String boundary = getBoundary();
            contentTypeArgs.put("boundary", boundary);
            MediaType mediaType = new MediaType( "multipart", "related", contentTypeArgs );
            headers.setContentType( mediaType);

            Integer frameNumber = getFrameNumber( request);
            String contentLocation = getContentLocation( request);

            // write preamble, just CRLF if empty.
            // DICOM Part 18 seems to ignore this.
            // outputMessage.getBody().write( "\r\n".getBytes());

            for ( DicomObjectI dicomPart: dicomParts) {

                HttpMessageConverter converter = getConverter( dicomPart.getClass(), partMediaType);
                
                if( converter == null) {
                    handleNoConverterFound(dicomPart.getClass(), partMediaType);
                }

                outputMessage.getBody().write( ("--"+ boundary + "\r\n").getBytes());
                outputMessage.getBody().write( ("Content-Location: " + contentLocation + "\r\n").getBytes());

//                outputMessage.getBody().write( ("Content-Type: application/dicom\r\n\r\n").getBytes());
//                transCoder.transcode( dicomPart, tsuid, outputMessage.getBody());

//                converter.write( dicomPart, MediaType.APPLICATION_OCTET_STREAM, outputMessage);
                writeFrameToPart( dicomPart, frameNumber, outputMessage);
            }
            outputMessage.getBody().write( ("\r\n--"+ boundary + "--\r\n\r\n").getBytes());

        } catch (IOException e) {
            String msg = "Error streaming dicom.";
            throw new IOException(msg, e);
        } catch( TransCoderException e) {
            throw new HttpMessageNotWritableException(e.getMessage(), e);
        }
    }

    private String getContentLocation(HttpServletRequest request) {
        String host = preferences.getSiteUrl();
        return host + request.getRequestURI();
    }

    private Integer getFrameNumber(HttpServletRequest request) {
        final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String s = pathVariables.get("frameNumber");
        return (s != null)? Integer.valueOf(s): null;
    }

    private void handleNoConverterFound(Class<?> aClass, MediaType mediaType) {
    }

    private HttpMessageConverter<DicomObjectI> getConverter(Class<?> clazz, MediaType mediaType) {
        for( HttpMessageConverter converter: _converters) {
            if( converter.canWrite( clazz, mediaType)) {
                return converter;
            }
        }
        return null;

//        return new DicomObjectMessageConverter();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom( clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        MediaType partMediaType = getPartType( mediaType);
        return APPLICATION_DICOM.isCompatibleWith( partMediaType)
                || APPLICATION_OCTET_STREAM.isCompatibleWith( partMediaType);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return MULTIPART_MIXED.isCompatibleWith(mediaType) || MULTIPART_RELATED.isCompatibleWith( mediaType);
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

    protected void writeFrameToPart(DicomObjectI dicomObject, int frameNumber, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        OutputStream os = httpOutputMessage.getBody();
        byte[] pixels = dicomObject.getPixels();
        os.write( ("Content-Type: application/octet-stream; transfer-syntax=1.2.840.10008.1.2.1\r\n").getBytes());
        String contentLengthHeader = String.format("Content-Length: %s\r\n", pixels.length);
        os.write( contentLengthHeader.getBytes());
        os.write( ("\r\n").getBytes());
        httpOutputMessage.getBody().write( dicomObject.getPixels());
    }


    public static void main(String[] args) {
        MediaType type = MultipartDicomFileMessageConverter.MULTIPART_RELATED;
        Map<String,String> map = new HashMap<>();
        map.put("type", "\"application/dicom\"");
        MediaType type2 = new MediaType("multipart", "related", map);
        System.out.println(type);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( new MediaType( "application", "dicom+xml"));
        headers.set("Content-ID", "1");

        System.out.println( headers);
    }
}

