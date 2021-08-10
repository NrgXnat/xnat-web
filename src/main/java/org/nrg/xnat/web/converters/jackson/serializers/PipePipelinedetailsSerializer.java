package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetails;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PipePipelinedetailsSerializer<T extends PipePipelinedetails> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7889582611940425972L;

    @SuppressWarnings("unchecked")
    public PipePipelinedetailsSerializer() {
        this((Class<T>) PipePipelinedetails.class);
    }

    protected PipePipelinedetailsSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "appliesto" property here: String
        // TODO: Write out the "customwebpage" property here: String
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "generateselements_element" property here: java.util.List
        // TODO: Write out the "parameters_parameter" property here: java.util.List
        // TODO: Write out the "path" property here: String
        // TODO: Write out the "schemaElementName" property here: String
    }
}

