package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVoiceaudioscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatVoiceaudioscandataSerializer<T extends XnatVoiceaudioscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -1321997158161274824L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatVoiceaudioscandataSerializer() {
        this((Class<T>) XnatVoiceaudioscandata.class);
    }

    protected XnatVoiceaudioscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

