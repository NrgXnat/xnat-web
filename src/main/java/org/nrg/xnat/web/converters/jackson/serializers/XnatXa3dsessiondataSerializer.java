package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXa3dsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXa3dsessiondataSerializer<T extends XnatXa3dsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 3278975389841106335L;

    @SuppressWarnings("unchecked")
    public XnatXa3dsessiondataSerializer() {
        this((Class<T>) XnatXa3dsessiondata.class);
    }

    protected XnatXa3dsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

