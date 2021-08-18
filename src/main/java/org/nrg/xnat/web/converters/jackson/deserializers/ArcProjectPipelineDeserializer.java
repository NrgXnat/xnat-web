package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectPipeline;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcProjectPipelineDeserializer<T extends ArcProjectPipeline> extends ArcPipelinedataDeserializer<T> {
    private static final long serialVersionUID = 8140386513131788703L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectPipelineDeserializer() {
        this((Class<T>) ArcProjectPipeline.class);
    }

    protected ArcProjectPipelineDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dependent":
                instance.setDependent(parser.getBooleanValue());
                break;
            case "stepId":
                instance.setStepid(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

