package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSmscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSmscandataSerializer<T extends XnatSmscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 5070012818848811914L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSmscandataSerializer() {
        this((Class<T>) XnatSmscandata.class);
    }

    protected XnatSmscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

