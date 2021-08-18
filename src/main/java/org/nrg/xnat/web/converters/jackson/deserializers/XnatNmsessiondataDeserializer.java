package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatNmsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatNmsessiondataDeserializer<T extends XnatNmsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 262114885905342851L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatNmsessiondataDeserializer() {
        this((Class<T>) XnatNmsessiondata.class);
    }

    protected XnatNmsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

