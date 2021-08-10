package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatScscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatScscandataDeserializer<T extends XnatScscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -5184931345671845364L;

    @SuppressWarnings("unchecked")
    public XnatScscandataDeserializer() {
        this((Class<T>) XnatScscandata.class);
    }

    protected XnatScscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        super.handleField(instance, field, parser, context);
    }
}
