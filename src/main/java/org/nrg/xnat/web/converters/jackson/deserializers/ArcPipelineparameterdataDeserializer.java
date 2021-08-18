package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelineparameterdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcPipelineparameterdataDeserializer<T extends ArcPipelineparameterdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8784882741662343956L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPipelineparameterdataDeserializer() {
        this((Class<T>) ArcPipelineparameterdata.class);
    }

    protected ArcPipelineparameterdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcPipelineparameterdataId":
                instance.setArcPipelineparameterdataId(parser.getIntValue());
                break;
            case "batchparam":
                instance.setBatchparam(parser.getBooleanValue());
                break;
            case "csvvalues":
                instance.setCsvvalues(parser.getText());
                break;
            case "csvvalues_selected":
                instance.setCsvvalues_selected(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "editable":
                instance.setEditable(parser.getBooleanValue());
                break;
            case "multiplevalues":
                instance.setMultiplevalues(parser.getBooleanValue());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "schemalink":
                instance.setSchemalink(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

