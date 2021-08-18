package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGenericdata;

import java.io.IOException;

@Slf4j
public abstract class XnatGenericdataSerializer<T extends XnatGenericdata> extends XnatExperimentdataSerializer<T> {
    private static final long serialVersionUID = -3201145954551111669L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGenericdataSerializer() {
        this((Class<T>) XnatGenericdata.class);
    }

    protected XnatGenericdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

