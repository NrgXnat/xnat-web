package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheckCondition;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValProtocoldataScanCheckConditionDeserializer<T extends ValProtocoldataScanCheckCondition> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -9104408575171571027L;

    @SuppressWarnings("unchecked")
    public ValProtocoldataScanCheckConditionDeserializer() {
        this((Class<T>) ValProtocoldataScanCheckCondition.class);
    }

    public ValProtocoldataScanCheckConditionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "diagnosis":
                // TODO: Handle the "diagnosis" property here: String
                break;
            case "expectedValue":
                // TODO: Handle the "expectedValue" property here: String
                break;
            case "foundValue":
                // TODO: Handle the "foundValue" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "status":
                // TODO: Handle the "status" property here: String
                break;
            case "valProtocoldataScanCheckConditionId":
                // TODO: Handle the "valProtocoldataScanCheckConditionId" property here: Integer
                break;
            case "verified":
                // TODO: Handle the "verified" property here: String
                break;
            case "xmlpath":
                // TODO: Handle the "xmlpath" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

