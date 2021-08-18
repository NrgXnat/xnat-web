package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatHdscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatHdscandataDeserializer<T extends XnatHdscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 2974383842885587738L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatHdscandataDeserializer() {
        this((Class<T>) XnatHdscandata.class);
    }

    protected XnatHdscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

