package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmvsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatGmvsessiondataDeserializer<T extends XnatGmvsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -7736593249903284590L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGmvsessiondataDeserializer() {
        this((Class<T>) XnatGmvsessiondata.class);
    }

    protected XnatGmvsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

