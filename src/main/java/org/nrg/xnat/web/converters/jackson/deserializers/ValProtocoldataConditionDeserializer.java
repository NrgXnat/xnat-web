package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataCondition;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValProtocoldataConditionDeserializer<T extends ValProtocoldataCondition> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -9176553933093036530L;

    @SuppressWarnings("unchecked")
    public ValProtocoldataConditionDeserializer() {
        this((Class<T>) ValProtocoldataCondition.class);
    }

    public ValProtocoldataConditionDeserializer(final Class<T> clazz) {
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
            case "valProtocoldataConditionId":
                // TODO: Handle the "valProtocoldataConditionId" property here: Integer
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

