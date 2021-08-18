package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatHdsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatHdsessiondataSerializer<T extends XnatHdsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -3201129361015444884L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatHdsessiondataSerializer() {
        this((Class<T>) XnatHdsessiondata.class);
    }

    protected XnatHdsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

