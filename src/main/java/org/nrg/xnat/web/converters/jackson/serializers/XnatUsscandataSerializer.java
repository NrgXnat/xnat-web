package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatUsscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatUsscandataSerializer<T extends XnatUsscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -5522939893058067428L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatUsscandataSerializer() {
        this((Class<T>) XnatUsscandata.class);
    }

    protected XnatUsscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

