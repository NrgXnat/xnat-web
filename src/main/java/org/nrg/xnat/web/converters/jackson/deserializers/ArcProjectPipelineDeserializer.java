package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectPipeline;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcProjectPipelineDeserializer<T extends ArcProjectPipeline> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7733147883171577569L;

    @SuppressWarnings("unchecked")
    public ArcProjectPipelineDeserializer() {
        this((Class<T>) ArcProjectPipeline.class);
    }

    public ArcProjectPipelineDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dependent":
                // TODO: Handle the "dependent" property here: Boolean
                break;
            case "pipelinedata":
                // TODO: Handle the "pipelinedata" property here: org.nrg.xdat.om.ArcPipelinedata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "stepid":
                // TODO: Handle the "stepid" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

