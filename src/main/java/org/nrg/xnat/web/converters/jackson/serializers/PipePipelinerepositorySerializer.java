package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinerepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PipePipelinerepositorySerializer<T extends PipePipelinerepository> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4536095292404161139L;

    @SuppressWarnings("unchecked")
    public PipePipelinerepositorySerializer() {
        this((Class<T>) PipePipelinerepository.class);
    }

    protected PipePipelinerepositorySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "generatedElementsByAllPipelines" property here: java.util.Hashtable
        // TODO: Write out the "pipePipelinerepositoryId" property here: Integer
        // TODO: Write out the "pipeline" property here: java.util.List
        // TODO: Write out the "pipelinesForDummyProject" property here: java.util.Hashtable
        // TODO: Write out the "schemaElementName" property here: String
    }
}

