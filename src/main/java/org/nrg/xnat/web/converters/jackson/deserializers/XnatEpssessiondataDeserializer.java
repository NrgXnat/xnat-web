package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEpssessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatEpssessiondataDeserializer<T extends XnatEpssessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 2108909177127475754L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEpssessiondataDeserializer() {
        this((Class<T>) XnatEpssessiondata.class);
    }

    protected XnatEpssessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

