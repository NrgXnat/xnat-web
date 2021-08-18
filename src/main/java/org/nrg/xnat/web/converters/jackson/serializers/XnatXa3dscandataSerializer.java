package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXa3dscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXa3dscandataSerializer<T extends XnatXa3dscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 1790253973474375491L;

    @SuppressWarnings("unchecked")
    public XnatXa3dscandataSerializer() {
        this((Class<T>) XnatXa3dscandata.class);
    }

    protected XnatXa3dscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

