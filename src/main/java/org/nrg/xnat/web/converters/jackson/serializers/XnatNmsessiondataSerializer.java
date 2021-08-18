package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatNmsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatNmsessiondataSerializer<T extends XnatNmsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -7658580734530005764L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatNmsessiondataSerializer() {
        this((Class<T>) XnatNmsessiondata.class);
    }

    protected XnatNmsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

