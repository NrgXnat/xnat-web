package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinerepository;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class PipePipelinerepositorySerializer<T extends PipePipelinerepository> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4536095292404161139L;

    @SuppressWarnings({"unchecked", "unused"})
    public PipePipelinerepositorySerializer() {
        this((Class<T>) PipePipelinerepository.class);
    }

    protected PipePipelinerepositorySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "pipePipelinerepositoryId", instance.getPipePipelinerepositoryId());
        // TODO: Write out the "pipeline" property here: java.util.List
    }
}

