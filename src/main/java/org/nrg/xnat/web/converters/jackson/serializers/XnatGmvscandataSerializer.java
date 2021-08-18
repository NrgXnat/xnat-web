package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmvscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatGmvscandataSerializer<T extends XnatGmvscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -2431027377608664056L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGmvscandataSerializer() {
        this((Class<T>) XnatGmvscandata.class);
    }

    protected XnatGmvscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

