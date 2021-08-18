package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntryMetafield;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatEntryMetafieldSerializer<T extends CatEntryMetafield> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6121253804234729800L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatEntryMetafieldSerializer() {
        this((Class<T>) CatEntryMetafield.class);
    }

    protected CatEntryMetafieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "catEntryMetafieldId", instance.getCatEntryMetafieldId());
        writeNonBlankField(generator, "metafield", instance.getMetafield());
        writeNonBlankField(generator, "name", instance.getName());
    }
}

