package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalogTag;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class CatCatalogTagDeserializer<T extends CatCatalogTag> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2371510844960506043L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatCatalogTagDeserializer() {
        this((Class<T>) CatCatalogTag.class);
    }

    protected CatCatalogTagDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catCatalogTagId":
                instance.setCatCatalogTagId(parser.getIntValue());
                break;
            case "tag":
                instance.setTag(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

