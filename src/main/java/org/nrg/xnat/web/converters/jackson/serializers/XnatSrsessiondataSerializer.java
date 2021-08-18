package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSrsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSrsessiondataSerializer<T extends XnatSrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 7517109943674195204L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSrsessiondataSerializer() {
        this((Class<T>) XnatSrsessiondata.class);
    }

    protected XnatSrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

