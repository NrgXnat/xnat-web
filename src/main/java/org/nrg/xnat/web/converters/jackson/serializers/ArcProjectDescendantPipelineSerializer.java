package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectDescendantPipeline;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcProjectDescendantPipelineSerializer<T extends ArcProjectDescendantPipeline> extends ArcPipelinedataSerializer<T> {
    private static final long serialVersionUID = 6292809798594113072L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectDescendantPipelineSerializer() {
        this((Class<T>) ArcProjectDescendantPipeline.class);
    }

    protected ArcProjectDescendantPipelineSerializer(final Class<T> clazz) {
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

