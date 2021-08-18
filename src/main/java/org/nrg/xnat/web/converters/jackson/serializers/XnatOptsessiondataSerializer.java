package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOptsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatOptsessiondataSerializer<T extends XnatOptsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -3488137373783947737L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOptsessiondataSerializer() {
        this((Class<T>) XnatOptsessiondata.class);
    }

    protected XnatOptsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

