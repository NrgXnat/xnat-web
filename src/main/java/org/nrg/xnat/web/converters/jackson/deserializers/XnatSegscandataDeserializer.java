package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSegscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSegscandataDeserializer<T extends XnatSegscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -2620011703850235950L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSegscandataDeserializer() {
        this((Class<T>) XnatSegscandata.class);
    }

    protected XnatSegscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

