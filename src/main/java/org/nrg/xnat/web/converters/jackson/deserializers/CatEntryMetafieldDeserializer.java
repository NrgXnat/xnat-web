package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntryMetafield;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CatEntryMetafieldDeserializer<T extends CatEntryMetafield> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7184480956876799366L;

    @SuppressWarnings("unchecked")
    public CatEntryMetafieldDeserializer() {
        this((Class<T>) CatEntryMetafield.class);
    }

    public CatEntryMetafieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catEntryMetafieldId":
                // TODO: Handle the "catEntryMetafieldId" property here: Integer
                break;
            case "metafield":
                // TODO: Handle the "metafield" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

