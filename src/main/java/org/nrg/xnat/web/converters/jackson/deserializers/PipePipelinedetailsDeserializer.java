package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetails;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PipePipelinedetailsDeserializer<T extends PipePipelinedetails> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 2781186105472523611L;

    @SuppressWarnings("unchecked")
    public PipePipelinedetailsDeserializer() {
        this((Class<T>) PipePipelinedetails.class);
    }

    public PipePipelinedetailsDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "appliesto":
                // TODO: Handle the "appliesto" property here: String
                break;
            case "customwebpage":
                // TODO: Handle the "customwebpage" property here: String
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "generateselements_element":
                // TODO: Handle the "generateselements_element" property here: java.util.List
                break;
            case "parameters_parameter":
                // TODO: Handle the "parameters_parameter" property here: java.util.List
                break;
            case "path":
                // TODO: Handle the "path" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

