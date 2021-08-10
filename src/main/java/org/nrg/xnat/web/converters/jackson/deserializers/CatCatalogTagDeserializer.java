package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatCatalogTag;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CatCatalogTagDeserializer<T extends CatCatalogTag> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7665984862167868296L;

    @SuppressWarnings("unchecked")
    public CatCatalogTagDeserializer() {
        this((Class<T>) CatCatalogTag.class);
    }

    public CatCatalogTagDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catCatalogTagId":
                // TODO: Handle the "catCatalogTagId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

