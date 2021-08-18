package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOtherdicomscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatOtherdicomscandataSerializer<T extends XnatOtherdicomscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 1319616199401608246L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOtherdicomscandataSerializer() {
        this((Class<T>) XnatOtherdicomscandata.class);
    }

    protected XnatOtherdicomscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

