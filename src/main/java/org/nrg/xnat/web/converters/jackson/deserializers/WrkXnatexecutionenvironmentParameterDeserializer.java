package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentParameter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkXnatexecutionenvironmentParameterDeserializer<T extends WrkXnatexecutionenvironmentParameter> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4271130597594228135L;

    @SuppressWarnings("unchecked")
    public WrkXnatexecutionenvironmentParameterDeserializer() {
        this((Class<T>) WrkXnatexecutionenvironmentParameter.class);
    }

    public WrkXnatexecutionenvironmentParameterDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "parameter":
                // TODO: Handle the "parameter" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "wrkXnatexecutionenvironmentParameterId":
                // TODO: Handle the "wrkXnatexecutionenvironmentParameterId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

