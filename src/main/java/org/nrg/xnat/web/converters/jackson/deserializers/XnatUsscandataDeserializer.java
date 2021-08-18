package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatUsscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatUsscandataDeserializer<T extends XnatUsscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -1085105682791894473L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatUsscandataDeserializer() {
        this((Class<T>) XnatUsscandata.class);
    }

    protected XnatUsscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

