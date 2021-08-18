package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSrscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSrscandataSerializer<T extends XnatSrscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 4338433450468326104L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSrscandataSerializer() {
        this((Class<T>) XnatSrscandata.class);
    }

    protected XnatSrscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

