package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEpsscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEpsscandataSerializer<T extends XnatEpsscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 5431474480961308565L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEpsscandataSerializer() {
        this((Class<T>) XnatEpsscandata.class);
    }

    protected XnatEpsscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

