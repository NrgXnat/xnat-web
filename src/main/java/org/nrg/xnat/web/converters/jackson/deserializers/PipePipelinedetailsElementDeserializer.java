package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetailsElement;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class PipePipelinedetailsElementDeserializer<T extends PipePipelinedetailsElement> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6979416425743968727L;

    @SuppressWarnings({"unchecked", "unused"})
    public PipePipelinedetailsElementDeserializer() {
        this((Class<T>) PipePipelinedetailsElement.class);
    }

    protected PipePipelinedetailsElementDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "element":
                instance.setElement(parser.getText());
                break;
            case "pipePipelinedetailsElementId":
                instance.setPipePipelinedetailsElementId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

