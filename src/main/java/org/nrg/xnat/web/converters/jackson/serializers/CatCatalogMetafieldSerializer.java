package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalogMetafield;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatCatalogMetafieldSerializer<T extends CatCatalogMetafield> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 623298866403681038L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatCatalogMetafieldSerializer() {
        this((Class<T>) CatCatalogMetafield.class);
    }

    protected CatCatalogMetafieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "catCatalogMetafieldId", instance.getCatCatalogMetafieldId());
        writeNonBlankField(generator, "metafield", instance.getMetafield());
        writeNonBlankField(generator, "name", instance.getName());
    }
}

