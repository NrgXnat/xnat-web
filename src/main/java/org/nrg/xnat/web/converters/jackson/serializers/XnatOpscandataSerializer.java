package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOpscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatOpscandataSerializer<T extends XnatOpscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -4016738499113513845L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOpscandataSerializer() {
        this((Class<T>) XnatOpscandata.class);
    }

    protected XnatOpscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

