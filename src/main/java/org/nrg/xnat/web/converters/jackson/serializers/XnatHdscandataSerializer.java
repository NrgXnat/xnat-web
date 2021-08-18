package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatHdscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatHdscandataSerializer<T extends XnatHdscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -3980886907410736159L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatHdscandataSerializer() {
        this((Class<T>) XnatHdscandata.class);
    }

    protected XnatHdscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

