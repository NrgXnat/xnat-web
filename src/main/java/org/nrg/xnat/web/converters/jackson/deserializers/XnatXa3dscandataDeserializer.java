package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXa3dscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatXa3dscandataDeserializer<T extends XnatXa3dscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 1194459328254995094L;

    @SuppressWarnings("unchecked")
    public XnatXa3dscandataDeserializer() {
        this((Class<T>) XnatXa3dscandata.class);
    }

    public XnatXa3dscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

