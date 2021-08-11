package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcPropertyDeserializer<T extends ArcProperty> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6786604577830545601L;

    @SuppressWarnings("unchecked")
    public ArcPropertyDeserializer() {
        this((Class<T>) ArcProperty.class);
    }

    public ArcPropertyDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcPropertyId":
                // TODO: Handle the "arcPropertyId" property here: Integer
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "property":
                // TODO: Handle the "property" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

