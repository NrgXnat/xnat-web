package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRfsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatRfsessiondataDeserializer<T extends XnatRfsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -5702217572520750530L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRfsessiondataDeserializer() {
        this((Class<T>) XnatRfsessiondata.class);
    }

    protected XnatRfsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

