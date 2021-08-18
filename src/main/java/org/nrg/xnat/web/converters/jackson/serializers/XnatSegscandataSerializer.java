package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSegscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSegscandataSerializer<T extends XnatSegscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -2129315131604851800L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSegscandataSerializer() {
        this((Class<T>) XnatSegscandata.class);
    }

    protected XnatSegscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

