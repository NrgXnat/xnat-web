package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelinedata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcPipelinedataDeserializer<T extends ArcPipelinedata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6357759436959142866L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPipelinedataDeserializer() {
        this((Class<T>) ArcPipelinedata.class);
    }

    protected ArcPipelinedataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcPipelinedataId":
                instance.setArcPipelinedataId(parser.getIntValue());
                break;
            case "customwebpage":
                instance.setCustomwebpage(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "displaytext":
                instance.setDisplaytext(parser.getText());
                break;
            case "location":
                instance.setLocation(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "parameters_parameter":
                // TODO: Handle the "parameters_parameter" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

