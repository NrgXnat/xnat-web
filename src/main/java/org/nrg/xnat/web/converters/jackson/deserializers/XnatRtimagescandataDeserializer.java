package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRtimagescandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatRtimagescandataDeserializer<T extends XnatRtimagescandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -1365672465330797968L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRtimagescandataDeserializer() {
        this((Class<T>) XnatRtimagescandata.class);
    }

    protected XnatRtimagescandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

