package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetails;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class PipePipelinedetailsDeserializer<T extends PipePipelinedetails> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 64419157421897032L;

    @SuppressWarnings({"unchecked", "unused"})
    public PipePipelinedetailsDeserializer() {
        this((Class<T>) PipePipelinedetails.class);
    }

    protected PipePipelinedetailsDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "appliesto":
                instance.setAppliesto(parser.getText());
                break;
            case "customwebpage":
                instance.setCustomwebpage(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "generateselements_element":
                // TODO: Handle the "generateselements_element" property here: java.util.List
                break;
            case "parameters_parameter":
                // TODO: Handle the "parameters_parameter" property here: java.util.List
                break;
            case "path":
                instance.setPath(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

