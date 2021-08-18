package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEsscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEsscandataSerializer<T extends XnatEsscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 5263642015888947125L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEsscandataSerializer() {
        this((Class<T>) XnatEsscandata.class);
    }

    protected XnatEsscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

