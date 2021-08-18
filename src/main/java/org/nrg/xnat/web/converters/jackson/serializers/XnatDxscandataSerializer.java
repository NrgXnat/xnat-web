package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDxscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatDxscandataSerializer<T extends XnatDxscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 287138691946046208L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDxscandataSerializer() {
        this((Class<T>) XnatDxscandata.class);
    }

    protected XnatDxscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

