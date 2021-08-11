package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectPipeline;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcProjectPipelineSerializer<T extends ArcProjectPipeline> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1153729867175359009L;

    @SuppressWarnings("unchecked")
    public ArcProjectPipelineSerializer() {
        this((Class<T>) ArcProjectPipeline.class);
    }

    protected ArcProjectPipelineSerializer(final Class<T> clazz) {
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

