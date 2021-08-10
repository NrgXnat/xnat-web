package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcess;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ProvProcessDeserializer<T extends ProvProcess> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 9030876025041241768L;

    @SuppressWarnings("unchecked")
    public ProvProcessDeserializer() {
        this((Class<T>) ProvProcess.class);
    }

    public ProvProcessDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "processstep":
                // TODO: Handle the "processstep" property here: java.util.List
                break;
            case "provProcessId":
                // TODO: Handle the "provProcessId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

