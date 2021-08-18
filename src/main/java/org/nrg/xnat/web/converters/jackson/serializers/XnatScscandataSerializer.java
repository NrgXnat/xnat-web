package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatScscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatScscandataSerializer<T extends XnatScscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -6776644954683068702L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatScscandataSerializer() {
        this((Class<T>) XnatScscandata.class);
    }

    protected XnatScscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

