package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectDescendantPipeline;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcProjectDescendantPipelineSerializer<T extends ArcProjectDescendantPipeline> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7048943414482382712L;

    @SuppressWarnings("unchecked")
    public ArcProjectDescendantPipelineSerializer() {
        this((Class<T>) ArcProjectDescendantPipeline.class);
    }

    protected ArcProjectDescendantPipelineSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "dependent" property here: Boolean
        // TODO: Write out the "pipelinedata" property here: org.nrg.xdat.om.ArcPipelinedata
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "stepid" property here: String
    }
}

