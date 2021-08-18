package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmvsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatGmvsessiondataSerializer<T extends XnatGmvsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -1288856041104737598L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGmvsessiondataSerializer() {
        this((Class<T>) XnatGmvsessiondata.class);
    }

    protected XnatGmvsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

