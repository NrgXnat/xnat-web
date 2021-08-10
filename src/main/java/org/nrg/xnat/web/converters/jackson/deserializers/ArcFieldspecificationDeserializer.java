package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcFieldspecification;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcFieldspecificationDeserializer<T extends ArcFieldspecification> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2771505185515800400L;

    @SuppressWarnings("unchecked")
    public ArcFieldspecificationDeserializer() {
        this((Class<T>) ArcFieldspecification.class);
    }

    public ArcFieldspecificationDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcFieldspecificationId":
                // TODO: Handle the "arcFieldspecificationId" property here: Integer
                break;
            case "fieldspecification":
                // TODO: Handle the "fieldspecification" property here: String
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

