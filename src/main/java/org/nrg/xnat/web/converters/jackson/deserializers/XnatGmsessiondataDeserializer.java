package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatGmsessiondataDeserializer<T extends XnatGmsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 8184771641696672089L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGmsessiondataDeserializer() {
        this((Class<T>) XnatGmsessiondata.class);
    }

    protected XnatGmsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

