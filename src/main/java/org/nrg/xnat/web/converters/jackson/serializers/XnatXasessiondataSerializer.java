package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXasessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXasessiondataSerializer<T extends XnatXasessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -5958099638139389869L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXasessiondataSerializer() {
        this((Class<T>) XnatXasessiondata.class);
    }

    protected XnatXasessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

