package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinerepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PipePipelinerepositoryDeserializer<T extends PipePipelinerepository> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4844319297310328824L;

    @SuppressWarnings("unchecked")
    public PipePipelinerepositoryDeserializer() {
        this((Class<T>) PipePipelinerepository.class);
    }

    public PipePipelinerepositoryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "generatedElementsByAllPipelines":
                // TODO: Handle the "generatedElementsByAllPipelines" property here: java.util.Hashtable
                break;
            case "pipePipelinerepositoryId":
                // TODO: Handle the "pipePipelinerepositoryId" property here: Integer
                break;
            case "pipeline":
                // TODO: Handle the "pipeline" property here: java.util.List
                break;
            case "pipelinesForDummyProject":
                // TODO: Handle the "pipelinesForDummyProject" property here: java.util.Hashtable
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

