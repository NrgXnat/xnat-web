package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheck;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValProtocoldataScanCheckDeserializer<T extends ValProtocoldataScanCheck> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4222376094684393015L;

    @SuppressWarnings("unchecked")
    public ValProtocoldataScanCheckDeserializer() {
        this((Class<T>) ValProtocoldataScanCheck.class);
    }

    public ValProtocoldataScanCheckDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "additionalval":
                // TODO: Handle the "additionalval" property here: org.nrg.xdat.model.ValAdditionalvalI
                break;
            case "comments_comment":
                // TODO: Handle the "comments_comment" property here: java.util.List
                break;
            case "conditions_condition":
                // TODO: Handle the "conditions_condition" property here: java.util.List
                break;
            case "scanId":
                // TODO: Handle the "scanId" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "status":
                // TODO: Handle the "status" property here: String
                break;
            case "summary":
                // TODO: Handle the "summary" property here: String
                break;
            case "valProtocoldataScanCheckId":
                // TODO: Handle the "valProtocoldataScanCheckId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

