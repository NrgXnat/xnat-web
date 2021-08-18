package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEsvscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEsvscandataSerializer<T extends XnatEsvscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -9017668533044169462L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEsvscandataSerializer() {
        this((Class<T>) XnatEsvscandata.class);
    }

    protected XnatEsvscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

