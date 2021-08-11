package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelinedata;

import java.io.IOException;

@Slf4j
public abstract class ArcPipelinedataDeserializer<T extends ArcPipelinedata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6130106489474567474L;

    protected ArcPipelinedataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcPipelinedataId":
                // TODO: Handle the "arcPipelinedataId" property here: Integer
                break;
            case "customwebpage":
                // TODO: Handle the "customwebpage" property here: String
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "displaytext":
                // TODO: Handle the "displaytext" property here: String
                break;
            case "location":
                // TODO: Handle the "location" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "parameters_parameter":
                // TODO: Handle the "parameters_parameter" property here: java.util.List
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

