package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVoiceaudioscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatVoiceaudioscandataDeserializer<T extends XnatVoiceaudioscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 7130997924474468480L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatVoiceaudioscandataDeserializer() {
        this((Class<T>) XnatVoiceaudioscandata.class);
    }

    protected XnatVoiceaudioscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

