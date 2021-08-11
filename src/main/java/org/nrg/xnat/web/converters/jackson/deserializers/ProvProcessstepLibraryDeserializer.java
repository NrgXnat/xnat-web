package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcessstepLibrary;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ProvProcessstepLibraryDeserializer<T extends ProvProcessstepLibrary> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7361028737057080240L;

    @SuppressWarnings("unchecked")
    public ProvProcessstepLibraryDeserializer() {
        this((Class<T>) ProvProcessstepLibrary.class);
    }

    public ProvProcessstepLibraryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "library":
                // TODO: Handle the "library" property here: String
                break;
            case "provProcessstepLibraryId":
                // TODO: Handle the "provProcessstepLibraryId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "version":
                // TODO: Handle the "version" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

