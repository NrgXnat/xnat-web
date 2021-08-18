package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatIoscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatIoscandataDeserializer<T extends XnatIoscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 7790531216522282424L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatIoscandataDeserializer() {
        this((Class<T>) XnatIoscandata.class);
    }

    protected XnatIoscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

