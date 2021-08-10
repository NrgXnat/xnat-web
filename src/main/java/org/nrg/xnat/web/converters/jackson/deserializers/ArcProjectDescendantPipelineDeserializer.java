package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectDescendantPipeline;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcProjectDescendantPipelineDeserializer<T extends ArcProjectDescendantPipeline> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4974533576886532482L;

    @SuppressWarnings("unchecked")
    public ArcProjectDescendantPipelineDeserializer() {
        this((Class<T>) ArcProjectDescendantPipeline.class);
    }

    public ArcProjectDescendantPipelineDeserializer(final Class<T> clazz) {
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

