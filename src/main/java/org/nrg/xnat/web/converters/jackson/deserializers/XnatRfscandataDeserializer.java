package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRfscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatRfscandataDeserializer<T extends XnatRfscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 6182958108342134525L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRfscandataDeserializer() {
        this((Class<T>) XnatRfscandata.class);
    }

    protected XnatRfscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

