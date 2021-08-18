package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatUssessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatUssessiondataDeserializer<T extends XnatUssessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 3552811530726916494L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatUssessiondataDeserializer() {
        this((Class<T>) XnatUssessiondata.class);
    }

    protected XnatUssessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

