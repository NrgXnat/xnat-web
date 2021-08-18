package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEpssessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEpssessiondataSerializer<T extends XnatEpssessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 4824409133923000703L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEpssessiondataSerializer() {
        this((Class<T>) XnatEpssessiondata.class);
    }

    protected XnatEpssessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

