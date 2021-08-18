package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntryTag;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatEntryTagSerializer<T extends CatEntryTag> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2862114465266088637L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatEntryTagSerializer() {
        this((Class<T>) CatEntryTag.class);
    }

    protected CatEntryTagSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "catEntryTagId", instance.getCatEntryTagId());
        writeNonBlankField(generator, "tag", instance.getTag());
    }
}

