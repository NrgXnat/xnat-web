package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetailsParameter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PipePipelinedetailsParameterDeserializer<T extends PipePipelinedetailsParameter> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5970529540114528864L;

    @SuppressWarnings("unchecked")
    public PipePipelinedetailsParameterDeserializer() {
        this((Class<T>) PipePipelinedetailsParameter.class);
    }

    public PipePipelinedetailsParameterDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "pipePipelinedetailsParameterId":
                // TODO: Handle the "pipePipelinedetailsParameterId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "values_csvvalues":
                // TODO: Handle the "values_csvvalues" property here: String
                break;
            case "values_schemalink":
                // TODO: Handle the "values_schemalink" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

