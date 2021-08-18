package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectDescendantPipeline;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcProjectDescendantPipelineDeserializer<T extends ArcProjectDescendantPipeline> extends ArcPipelinedataDeserializer<T> {
    private static final long serialVersionUID = 9200860657544650678L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectDescendantPipelineDeserializer() {
        this((Class<T>) ArcProjectDescendantPipeline.class);
    }

    protected ArcProjectDescendantPipelineDeserializer(final Class<T> clazz) {
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

