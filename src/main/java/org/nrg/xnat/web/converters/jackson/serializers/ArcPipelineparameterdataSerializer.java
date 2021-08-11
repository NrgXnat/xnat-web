package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelineparameterdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcPipelineparameterdataSerializer<T extends ArcPipelineparameterdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6592465787406739011L;

    @SuppressWarnings("unchecked")
    public ArcPipelineparameterdataSerializer() {
        this((Class<T>) ArcPipelineparameterdata.class);
    }

    protected ArcPipelineparameterdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "arcPipelineparameterdataId" property here: Integer
        // TODO: Write out the "batchparam" property here: Boolean
        // TODO: Write out the "csvvalues" property here: String
        // TODO: Write out the "csvvalues_selected" property here: String
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "editable" property here: Boolean
        // TODO: Write out the "multiplevalues" property here: Boolean
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "schemalink" property here: String
    }
}

