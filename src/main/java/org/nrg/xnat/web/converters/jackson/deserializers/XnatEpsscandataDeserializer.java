package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEpsscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatEpsscandataDeserializer<T extends XnatEpsscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -7885004180080300698L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEpsscandataDeserializer() {
        this((Class<T>) XnatEpsscandata.class);
    }

    protected XnatEpsscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

