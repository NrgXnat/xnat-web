package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatGmscandataSerializer<T extends XnatGmscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 8932723859321643441L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatGmscandataSerializer() {
        this((Class<T>) XnatGmscandata.class);
    }

    protected XnatGmscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

