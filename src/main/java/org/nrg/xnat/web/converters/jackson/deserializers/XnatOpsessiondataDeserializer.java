package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOpsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatOpsessiondataDeserializer<T extends XnatOpsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -5531675985219537939L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOpsessiondataDeserializer() {
        this((Class<T>) XnatOpsessiondata.class);
    }

    protected XnatOpsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

