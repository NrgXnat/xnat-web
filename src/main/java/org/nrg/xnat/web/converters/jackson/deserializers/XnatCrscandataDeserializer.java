package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCrscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatCrscandataDeserializer<T extends XnatCrscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -4503996180376498485L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCrscandataDeserializer() {
        this((Class<T>) XnatCrscandata.class);
    }

    protected XnatCrscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

