package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalogMetafield;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class CatCatalogMetafieldDeserializer<T extends CatCatalogMetafield> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8035435264368714749L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatCatalogMetafieldDeserializer() {
        this((Class<T>) CatCatalogMetafield.class);
    }

    protected CatCatalogMetafieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catCatalogMetafieldId":
                instance.setCatCatalogMetafieldId(parser.getIntValue());
                break;
            case "metafield":
                instance.setMetafield(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

