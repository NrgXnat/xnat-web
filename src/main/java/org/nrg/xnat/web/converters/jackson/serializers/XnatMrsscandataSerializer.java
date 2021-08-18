package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrsscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatMrsscandataSerializer<T extends XnatMrsscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 6140138077504036952L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrsscandataSerializer() {
        this((Class<T>) XnatMrsscandata.class);
    }

    protected XnatMrsscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

