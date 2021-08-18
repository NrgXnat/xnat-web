package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCrscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatCrscandataSerializer<T extends XnatCrscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 3463102987926091797L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCrscandataSerializer() {
        this((Class<T>) XnatCrscandata.class);
    }

    protected XnatCrscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

