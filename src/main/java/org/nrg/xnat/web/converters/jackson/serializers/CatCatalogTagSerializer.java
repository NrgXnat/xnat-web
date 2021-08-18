package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalogTag;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatCatalogTagSerializer<T extends CatCatalogTag> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6902667040728100676L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatCatalogTagSerializer() {
        this((Class<T>) CatCatalogTag.class);
    }

    protected CatCatalogTagSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "catCatalogTagId", instance.getCatCatalogTagId());
        writeNonBlankField(generator, "tag", instance.getTag());
    }
}

