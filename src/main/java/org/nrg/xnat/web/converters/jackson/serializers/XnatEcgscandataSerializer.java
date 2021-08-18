package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEcgscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEcgscandataSerializer<T extends XnatEcgscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 7837432765835815917L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEcgscandataSerializer() {
        this((Class<T>) XnatEcgscandata.class);
    }

    protected XnatEcgscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

