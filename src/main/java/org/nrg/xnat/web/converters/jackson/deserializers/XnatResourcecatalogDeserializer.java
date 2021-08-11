package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatResourcecatalogDeserializer<T extends XnatResourcecatalog> extends XnatResourceDeserializer<T> {
    private static final long serialVersionUID = 3231255490942201806L;

    @SuppressWarnings("unchecked")
    public XnatResourcecatalogDeserializer() {
        this((Class<T>) XnatResourcecatalog.class);
    }

    protected XnatResourcecatalogDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        super.handleField(instance, field, parser, context);
    }
}
