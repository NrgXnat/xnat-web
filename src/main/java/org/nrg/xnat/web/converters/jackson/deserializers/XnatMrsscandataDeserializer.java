package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrsscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatMrsscandataDeserializer<T extends XnatMrsscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 3800586741236806166L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrsscandataDeserializer() {
        this((Class<T>) XnatMrsscandata.class);
    }

    protected XnatMrsscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

