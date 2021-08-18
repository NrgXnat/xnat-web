package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMgscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatMgscandataSerializer<T extends XnatMgscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 6345186155667871L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMgscandataSerializer() {
        this((Class<T>) XnatMgscandata.class);
    }

    protected XnatMgscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

