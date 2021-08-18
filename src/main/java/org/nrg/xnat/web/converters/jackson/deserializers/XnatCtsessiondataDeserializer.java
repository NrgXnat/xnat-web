package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatCtsessiondataDeserializer<T extends XnatCtsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -8135058704336239875L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCtsessiondataDeserializer() {
        this((Class<T>) XnatCtsessiondata.class);
    }

    protected XnatCtsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

