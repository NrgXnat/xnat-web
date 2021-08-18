package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMegsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatMegsessiondataDeserializer<T extends XnatMegsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 5876157480718023468L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMegsessiondataDeserializer() {
        this((Class<T>) XnatMegsessiondata.class);
    }

    protected XnatMegsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

