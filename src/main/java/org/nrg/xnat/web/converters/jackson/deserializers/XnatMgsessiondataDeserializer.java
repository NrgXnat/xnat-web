package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMgsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatMgsessiondataDeserializer<T extends XnatMgsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 8170307225071444989L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMgsessiondataDeserializer() {
        this((Class<T>) XnatMgsessiondata.class);
    }

    protected XnatMgsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

