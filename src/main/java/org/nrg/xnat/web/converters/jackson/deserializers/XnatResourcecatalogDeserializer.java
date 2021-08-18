package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourcecatalog;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatResourcecatalogDeserializer<T extends XnatResourcecatalog> extends XnatResourceDeserializer<T> {
    private static final long serialVersionUID = -4757563010924046945L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatResourcecatalogDeserializer() {
        this((Class<T>) XnatResourcecatalog.class);
    }

    protected XnatResourcecatalogDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

