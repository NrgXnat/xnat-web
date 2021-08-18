package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtqcscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatCtqcscandataSerializer<T extends XnatCtqcscandata> extends XnatQcscandataSerializer<T> {
    private static final long serialVersionUID = 5690805766577186823L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCtqcscandataSerializer() {
        this((Class<T>) XnatCtqcscandata.class);
    }

    protected XnatCtqcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "ct", instance.getCt());
        super.serializeImpl(instance, generator, provider);
    }
}

