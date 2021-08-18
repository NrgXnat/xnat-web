package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourcecatalog;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatResourcecatalogSerializer<T extends XnatResourcecatalog> extends XnatResourceSerializer<T> {
    private static final long serialVersionUID = 4333210263304575653L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatResourcecatalogSerializer() {
        this((Class<T>) XnatResourcecatalog.class);
    }

    protected XnatResourcecatalogSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

