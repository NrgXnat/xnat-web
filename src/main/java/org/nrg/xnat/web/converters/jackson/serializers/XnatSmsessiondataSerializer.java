package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSmsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSmsessiondataSerializer<T extends XnatSmsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -868878208321575139L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSmsessiondataSerializer() {
        this((Class<T>) XnatSmsessiondata.class);
    }

    protected XnatSmsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

