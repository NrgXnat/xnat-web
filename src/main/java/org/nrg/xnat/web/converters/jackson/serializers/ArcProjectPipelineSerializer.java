package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectPipeline;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcProjectPipelineSerializer<T extends ArcProjectPipeline> extends ArcPipelinedataSerializer<T> {
    private static final long serialVersionUID = 1123452373079025581L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectPipelineSerializer() {
        this((Class<T>) ArcProjectPipeline.class);
    }

    protected ArcProjectPipelineSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullBoolean(generator, "dependent", instance.getDependent());
        writeNonBlankField(generator, "stepid", instance.getStepid());
        super.serializeImpl(instance, generator, provider);
    }
}

