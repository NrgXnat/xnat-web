package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXcsessiondataSerializer<T extends XnatXcsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 6112377664677355956L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXcsessiondataSerializer() {
        this((Class<T>) XnatXcsessiondata.class);
    }

    protected XnatXcsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

