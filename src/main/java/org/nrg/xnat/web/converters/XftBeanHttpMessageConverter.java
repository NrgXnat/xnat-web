package org.nrg.xnat.web.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.utilities.Reflection;
import org.nrg.xdat.bean.base.BaseElement;
import org.nrg.xdat.bean.reader.XDATXMLReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles converting XFT classes
 */
@Component
@Slf4j
public class XftBeanHttpMessageConverter extends AbstractHttpMessageConverter<BaseElement> {
    @Autowired
    public XftBeanHttpMessageConverter(final SerializerService serializer) {
        super(MediaType.APPLICATION_XML);
        _serializer = serializer;
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return BaseElement.class.isAssignableFrom(clazz);
    }

    @Override
    protected BaseElement readInternal(final Class<? extends BaseElement> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    	final MediaType mediaType = getPreferredMediaType(inputMessage.getHeaders());
        try (final InputStream body = inputMessage.getBody()) {
            // return mediaType == MediaType.APPLICATION_XML ? readXmlBody(body) : readJsonBody(clazz, body);
            return mediaType == MediaType.APPLICATION_XML ? readXmlBody(body) : readJsonBodyWithDeserializer(clazz, body, getPreferredCharset(inputMessage.getHeaders()));
        }
    }

    @Override
    protected void writeInternal(final BaseElement baseElement, final HttpOutputMessage outputMessage) throws HttpMessageNotWritableException, IOException {
    	final MediaType mediaType = getPreferredMediaType(outputMessage.getHeaders());
        try (final OutputStream body = outputMessage.getBody()) {
            if (mediaType == MediaType.APPLICATION_XML) {
                writeXmlBody(baseElement, body);
            } else {
                // writeJsonBody(baseElement, body, getPreferredCharset(outputMessage.getHeaders()));
                writeJsonBodyWithSerializer(baseElement, body, getPreferredCharset(outputMessage.getHeaders()));
            }
        }
    }

    private BaseElement readXmlBody(final InputStream body) throws IOException {
        final XDATXMLReader reader = new XDATXMLReader();
        try {
            return reader.parse(body);
        } catch (SAXException e) {
            throw new HttpMessageNotReadableException("Couldn't read the requested input", e);
        }
    }

    @SuppressWarnings("unused")
    private BaseElement readJsonBody(final Class<? extends BaseElement> clazz, final InputStream body) throws IOException {
        final JsonNode deserialized = _serializer.deserializeJson(body);
        final Map<String, Object> mapped = _serializer.getObjectMapper().convertValue(deserialized, new TypeReference<HashMap<String, Object>>() {
        });

        try {
            // Populate properties from JSON here. Not sure how well this will work!
            final BaseElement baseElement = clazz.newInstance();
            BeanUtils.populate(baseElement, mapped);
            return baseElement;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new HttpMessageNotReadableException("Can't create an instance of " + clazz.getName() + " from the default constructor", e);
        }
    }

    private BaseElement readJsonBodyWithDeserializer(final Class<? extends BaseElement> clazz, final InputStream body, final Charset charset) throws IOException {
        // This delegates deserialization to the Jackson deserializer implementation, e.g. XnatSubjectdataDeserializer if the element is XnatSubjectdata.
        return _serializer.deserializeJson(IOUtils.toString(body, charset.toString()), clazz);
    }

    private void writeXmlBody(final BaseElement baseElement, final OutputStream body) {
        try (final Writer output = new OutputStreamWriter(body)) {
            baseElement.toXML(output);
        } catch (Exception e) {
            throw new HttpMessageNotWritableException("An error occurred trying to write an object of type " + baseElement.getClass().getName(), e);
        }
    }

    @SuppressWarnings("unused")
    private void writeJsonBody(final BaseElement baseElement, final OutputStream body, final Charset charset) throws IOException {
        final Class<? extends BaseElement> clazz  = baseElement.getClass();
        final ObjectMapper                 mapper = _serializer.getObjectMapper();
        try (final JsonGenerator generator = mapper.getFactory().createGenerator(body)) {
            generator.writeStartObject();
            final List<Method> getters = Reflection.getGetters(clazz).stream().filter(getter -> Modifier.isPublic(getter.getModifiers())).collect(Collectors.toList());
            for (final Method getter : getters) {
                final String   property   = StringUtils.uncapitalize(StringUtils.removeStart(getter.getName(), "get"));
                final Class<?> returnType = getter.getReturnType();
                if (returnType.equals(String.class)) {
                    generator.writeStringField(property, (String) getter.invoke(baseElement));
                } else if (returnType.equals(Integer.TYPE) || returnType.equals(Integer.class)) {
                    generator.writeNumberField(property, (Integer) getter.invoke(baseElement));
                } else if (returnType.equals(Double.TYPE) || returnType.equals(Double.class)) {
                    generator.writeNumberField(property, (Double) getter.invoke(baseElement));
                } else if (returnType.equals(Float.TYPE) || returnType.equals(Float.class)) {
                    generator.writeNumberField(property, (Float) getter.invoke(baseElement));
                } else if (returnType.equals(Boolean.TYPE) || returnType.equals(Boolean.class)) {
                    generator.writeBooleanField(property, (Boolean) getter.invoke(baseElement));
                }
                // Handle all return types here...
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new HttpMessageNotWritableException("An error occurred trying to write an object of type " + clazz.getName(), e);
        }
    }

    // This delegates serialization to the Jackson serializer implementation, e.g. XnatSubjectdataSerializer if the element is XnatSubjectdata.
    private void writeJsonBodyWithSerializer(final BaseElement baseElement, final OutputStream body, final Charset charset) throws IOException {
        // This requires a serializer to be configured with Jackson: see
        IOUtils.copy(new StringReader(_serializer.getObjectMapper().writeValueAsString(baseElement)), body, charset);
    }

    private MediaType getPreferredMediaType(final HttpHeaders headers) throws IOException {
        return headers.getAccept().stream()
                      .filter(mediaType -> getSupportedMediaTypes().contains(mediaType))
                      .findFirst()
                      .orElseThrow(() -> new IOException("This converter doesn't support any of the requested media types: " + getSupportedMediaTypes().stream().map(MediaType::toString).collect(Collectors.joining(", "))));
    }

    private Charset getPreferredCharset(final HttpHeaders headers) {
        final List<Charset> charsets = headers.getAcceptCharset();
        return charsets.isEmpty() ? Charset.defaultCharset() : charsets.get(0);
    }

    private final SerializerService _serializer;
}
