package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetailsParameter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PipePipelinedetailsParameterSerializer<T extends PipePipelinedetailsParameter> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6853313067330682979L;

    @SuppressWarnings("unchecked")
    public PipePipelinedetailsParameterSerializer() {
        this((Class<T>) PipePipelinedetailsParameter.class);
    }

    protected PipePipelinedetailsParameterSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "pipePipelinedetailsParameterId" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "values_csvvalues" property here: String
        // TODO: Write out the "values_schemalink" property here: String
    }
}

