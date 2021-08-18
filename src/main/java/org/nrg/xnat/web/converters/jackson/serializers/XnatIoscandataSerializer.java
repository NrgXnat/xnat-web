package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatIoscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatIoscandataSerializer<T extends XnatIoscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -941583660483385713L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatIoscandataSerializer() {
        this((Class<T>) XnatIoscandata.class);
    }

    protected XnatIoscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

