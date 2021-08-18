package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRtsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatRtsessiondataSerializer<T extends XnatRtsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 6404342048602807216L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRtsessiondataSerializer() {
        this((Class<T>) XnatRtsessiondata.class);
    }

    protected XnatRtsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

