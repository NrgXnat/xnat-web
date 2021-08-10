package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelineparameterdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcPipelineparameterdataDeserializer<T extends ArcPipelineparameterdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -3640730857137333623L;

    @SuppressWarnings("unchecked")
    public ArcPipelineparameterdataDeserializer() {
        this((Class<T>) ArcPipelineparameterdata.class);
    }

    public ArcPipelineparameterdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcPipelineparameterdataId":
                // TODO: Handle the "arcPipelineparameterdataId" property here: Integer
                break;
            case "batchparam":
                // TODO: Handle the "batchparam" property here: Boolean
                break;
            case "csvvalues":
                // TODO: Handle the "csvvalues" property here: String
                break;
            case "csvvalues_selected":
                // TODO: Handle the "csvvalues_selected" property here: String
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "editable":
                // TODO: Handle the "editable" property here: Boolean
                break;
            case "multiplevalues":
                // TODO: Handle the "multiplevalues" property here: Boolean
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "schemalink":
                // TODO: Handle the "schemalink" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

