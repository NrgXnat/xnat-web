package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalog;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatCatalogSerializer<T extends CatCatalog> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1377203841700765335L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatCatalogSerializer() {
        this((Class<T>) CatCatalog.class);
    }

    protected CatCatalogSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "catCatalogId", instance.getCatCatalogId());
        writeNonBlankField(generator, "description", instance.getDescription());
        // TODO: Write out the "entries_entry" property here: java.util.List
        writeNonBlankField(generator, "id", instance.getId());
        // TODO: Write out the "metafields_metafield" property here: java.util.List
        writeNonBlankField(generator, "name", instance.getName());
        // TODO: Write out the "sets_entryset" property here: java.util.List
        // TODO: Write out the "tags_tag" property here: java.util.List
    }
}

