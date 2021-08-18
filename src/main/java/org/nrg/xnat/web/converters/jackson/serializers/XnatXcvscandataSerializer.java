package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcvscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXcvscandataSerializer<T extends XnatXcvscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -2769474415724404450L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXcvscandataSerializer() {
        this((Class<T>) XnatXcvscandata.class);
    }

    protected XnatXcvscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

