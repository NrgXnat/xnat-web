package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinerepository;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class PipePipelinerepositoryDeserializer<T extends PipePipelinerepository> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4400929670864960246L;

    @SuppressWarnings({"unchecked", "unused"})
    public PipePipelinerepositoryDeserializer() {
        this((Class<T>) PipePipelinerepository.class);
    }

    protected PipePipelinerepositoryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "pipePipelinerepositoryId":
                instance.setPipePipelinerepositoryId(parser.getIntValue());
                break;
            case "pipeline":
                // TODO: Handle the "pipeline" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

