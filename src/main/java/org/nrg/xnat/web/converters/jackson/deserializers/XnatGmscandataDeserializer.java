package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatGmscandataDeserializer<T extends XnatGmscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 6281634306056603244L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGmscandataDeserializer() {
        this((Class<T>) XnatGmscandata.class);
    }

    protected XnatGmscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

