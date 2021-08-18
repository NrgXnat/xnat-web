package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetailsParameter;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class PipePipelinedetailsParameterSerializer<T extends PipePipelinedetailsParameter> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6853313067330682979L;

    @SuppressWarnings({"unchecked", "unused"})
    public PipePipelinedetailsParameterSerializer() {
        this((Class<T>) PipePipelinedetailsParameter.class);
    }

    protected PipePipelinedetailsParameterSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "pipePipelinedetailsParameterId", instance.getPipePipelinedetailsParameterId());
        writeNonBlankField(generator, "values_csvvalues", instance.getValues_csvvalues());
        writeNonBlankField(generator, "values_schemalink", instance.getValues_schemalink());
    }
}

