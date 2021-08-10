package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntryTag;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CatEntryTagDeserializer<T extends CatEntryTag> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1070237026336849913L;

    @SuppressWarnings("unchecked")
    public CatEntryTagDeserializer() {
        this((Class<T>) CatEntryTag.class);
    }

    public CatEntryTagDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catEntryTagId":
                // TODO: Handle the "catEntryTagId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

