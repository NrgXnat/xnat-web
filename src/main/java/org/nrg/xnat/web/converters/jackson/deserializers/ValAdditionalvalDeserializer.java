package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValAdditionalval;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValAdditionalvalDeserializer<T extends ValAdditionalval> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -370633617947947789L;

    @SuppressWarnings("unchecked")
    public ValAdditionalvalDeserializer() {
        this((Class<T>) ValAdditionalval.class);
    }

    public ValAdditionalvalDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "valAdditionalvalId":
                // TODO: Handle the "valAdditionalvalId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

