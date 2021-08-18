package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOptsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatOptsessiondataDeserializer<T extends XnatOptsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 8427983595288154293L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOptsessiondataDeserializer() {
        this((Class<T>) XnatOptsessiondata.class);
    }

    protected XnatOptsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

