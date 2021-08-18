package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRtimagescandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatRtimagescandataSerializer<T extends XnatRtimagescandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 8678299831504074228L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRtimagescandataSerializer() {
        this((Class<T>) XnatRtimagescandata.class);
    }

    protected XnatRtimagescandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

