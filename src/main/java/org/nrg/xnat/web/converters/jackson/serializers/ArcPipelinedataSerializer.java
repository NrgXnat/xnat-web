package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelinedata;

import java.io.IOException;

@Slf4j
public abstract class ArcPipelinedataSerializer<T extends ArcPipelinedata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1754414931284522029L;

    @SuppressWarnings("unchecked")
    public ArcPipelinedataSerializer() {
        this((Class<T>) ArcPipelinedata.class);
    }

    protected ArcPipelinedataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "arcPipelinedataId" property here: Integer
        // TODO: Write out the "customwebpage" property here: String
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "displaytext" property here: String
        // TODO: Write out the "location" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "parameters_parameter" property here: java.util.List
        // TODO: Write out the "schemaElementName" property here: String
    }
}

