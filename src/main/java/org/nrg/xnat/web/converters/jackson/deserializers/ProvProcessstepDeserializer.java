package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcessstep;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ProvProcessstepDeserializer<T extends ProvProcessstep> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7821397282363558043L;

    @SuppressWarnings("unchecked")
    public ProvProcessstepDeserializer() {
        this((Class<T>) ProvProcessstep.class);
    }

    public ProvProcessstepDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "compiler":
                // TODO: Handle the "compiler" property here: String
                break;
            case "compiler_version":
                // TODO: Handle the "compiler_version" property here: String
                break;
            case "library":
                // TODO: Handle the "library" property here: java.util.List
                break;
            case "machine":
                // TODO: Handle the "machine" property here: String
                break;
            case "platform":
                // TODO: Handle the "platform" property here: String
                break;
            case "platform_version":
                // TODO: Handle the "platform_version" property here: String
                break;
            case "program":
                // TODO: Handle the "program" property here: String
                break;
            case "program_arguments":
                // TODO: Handle the "program_arguments" property here: String
                break;
            case "program_version":
                // TODO: Handle the "program_version" property here: String
                break;
            case "provProcessstepId":
                // TODO: Handle the "provProcessstepId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "timestamp":
                // TODO: Handle the "timestamp" property here: Object
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

