package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalog;

import java.io.IOException;

@Slf4j
public abstract class CatCatalogSerializer<T extends CatCatalog> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2266622666110073853L;

    @SuppressWarnings("unchecked")
    public CatCatalogSerializer() {
        this((Class<T>) CatCatalog.class);
    }

    protected CatCatalogSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "catCatalogId" property here: Integer
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "entries_entry" property here: java.util.List
        // TODO: Write out the "metafields_metafield" property here: java.util.List
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "sets_entryset" property here: java.util.List
        // TODO: Write out the "tags_tag" property here: java.util.List
    }
}

