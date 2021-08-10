package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetailsElement;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PipePipelinedetailsElementDeserializer<T extends PipePipelinedetailsElement> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2845222272908351447L;

    @SuppressWarnings("unchecked")
    public PipePipelinedetailsElementDeserializer() {
        this((Class<T>) PipePipelinedetailsElement.class);
    }

    public PipePipelinedetailsElementDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "element":
                // TODO: Handle the "element" property here: String
                break;
            case "pipePipelinedetailsElementId":
                // TODO: Handle the "pipePipelinedetailsElementId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

