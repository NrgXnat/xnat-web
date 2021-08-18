package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatDcmentry;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatDcmentrySerializer<T extends CatDcmentry> extends CatEntrySerializer<T> {
    private static final long serialVersionUID = -8826849348828956920L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatDcmentrySerializer() {
        this((Class<T>) CatDcmentry.class);
    }

    protected CatDcmentrySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "instancenumber", instance.getInstancenumber());
        writeNonBlankField(generator, "uid", instance.getUid());
        super.serializeImpl(instance, generator, provider);
    }
}

