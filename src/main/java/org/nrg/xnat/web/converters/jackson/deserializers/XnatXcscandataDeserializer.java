package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatXcscandataDeserializer<T extends XnatXcscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 1904934266658975840L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXcscandataDeserializer() {
        this((Class<T>) XnatXcscandata.class);
    }

    protected XnatXcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

