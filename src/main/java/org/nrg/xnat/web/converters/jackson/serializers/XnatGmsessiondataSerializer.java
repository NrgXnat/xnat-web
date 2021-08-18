package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatGmsessiondataSerializer<T extends XnatGmsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 8689404679891097029L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGmsessiondataSerializer() {
        this((Class<T>) XnatGmsessiondata.class);
    }

    protected XnatGmsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

