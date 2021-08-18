package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcessstepLibrary;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ProvProcessstepLibrarySerializer<T extends ProvProcessstepLibrary> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5148774910947159070L;

    @SuppressWarnings({"unchecked", "unused"})
    public ProvProcessstepLibrarySerializer() {
        this((Class<T>) ProvProcessstepLibrary.class);
    }

    protected ProvProcessstepLibrarySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "library", instance.getLibrary());
        writeNonNullNumber(generator, "provProcessstepLibraryId", instance.getProvProcessstepLibraryId());
        writeNonBlankField(generator, "version", instance.getVersion());
    }
}

