package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXcscandataSerializer<T extends XnatXcscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -8723434063271913349L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXcscandataSerializer() {
        this((Class<T>) XnatXcscandata.class);
    }

    protected XnatXcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

