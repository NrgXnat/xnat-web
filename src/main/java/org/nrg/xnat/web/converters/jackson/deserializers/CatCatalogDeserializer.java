package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalog;

import java.io.IOException;

@Slf4j
public abstract class CatCatalogDeserializer<T extends CatCatalog> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3822624066199110750L;

    protected CatCatalogDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catCatalogId":
                // TODO: Handle the "catCatalogId" property here: Integer
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "entries_entry":
                // TODO: Handle the "entries_entry" property here: java.util.List
                break;
            case "metafields_metafield":
                // TODO: Handle the "metafields_metafield" property here: java.util.List
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sets_entryset":
                // TODO: Handle the "sets_entryset" property here: java.util.List
                break;
            case "tags_tag":
                // TODO: Handle the "tags_tag" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

