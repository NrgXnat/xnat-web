package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSrscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSrscandataDeserializer<T extends XnatSrscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -275590947881697327L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSrscandataDeserializer() {
        this((Class<T>) XnatSrscandata.class);
    }

    protected XnatSrscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

