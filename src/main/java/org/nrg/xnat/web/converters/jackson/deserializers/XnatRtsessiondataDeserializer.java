package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRtsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatRtsessiondataDeserializer<T extends XnatRtsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 3047292944375812368L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRtsessiondataDeserializer() {
        this((Class<T>) XnatRtsessiondata.class);
    }

    protected XnatRtsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

