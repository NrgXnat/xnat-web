package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalog;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class CatCatalogDeserializer<T extends CatCatalog> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6571002377858573194L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatCatalogDeserializer() {
        this((Class<T>) CatCatalog.class);
    }

    protected CatCatalogDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catCatalogId":
                instance.setCatCatalogId(parser.getIntValue());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "entries_entry":
                // TODO: Handle the "entries_entry" property here: java.util.List
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "metafields_metafield":
                // TODO: Handle the "metafields_metafield" property here: java.util.List
                break;
            case "name":
                instance.setName(parser.getText());
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

