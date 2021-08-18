package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetailsParameter;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class PipePipelinedetailsParameterDeserializer<T extends PipePipelinedetailsParameter> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 635080994302023332L;

    @SuppressWarnings({"unchecked", "unused"})
    public PipePipelinedetailsParameterDeserializer() {
        this((Class<T>) PipePipelinedetailsParameter.class);
    }

    protected PipePipelinedetailsParameterDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "pipePipelinedetailsParameterId":
                instance.setPipePipelinedetailsParameterId(parser.getIntValue());
                break;
            case "values_csvvalues":
                instance.setValues_csvvalues(parser.getText());
                break;
            case "values_schemalink":
                instance.setValues_schemalink(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

