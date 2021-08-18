package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXa3dsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatXa3dsessiondataDeserializer<T extends XnatXa3dsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 6110089586492535171L;

    @SuppressWarnings("unchecked")
    public XnatXa3dsessiondataDeserializer() {
        this((Class<T>) XnatXa3dsessiondata.class);
    }

    public XnatXa3dsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

