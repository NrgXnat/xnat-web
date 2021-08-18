package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEsvsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEsvsessiondataSerializer<T extends XnatEsvsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -2097287698978273550L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEsvsessiondataSerializer() {
        this((Class<T>) XnatEsvsessiondata.class);
    }

    protected XnatEsvsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

