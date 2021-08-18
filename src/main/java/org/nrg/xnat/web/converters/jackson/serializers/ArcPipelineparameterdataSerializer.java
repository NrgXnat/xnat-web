package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelineparameterdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcPipelineparameterdataSerializer<T extends ArcPipelineparameterdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6592465787406739011L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPipelineparameterdataSerializer() {
        this((Class<T>) ArcPipelineparameterdata.class);
    }

    protected ArcPipelineparameterdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "arcPipelineparameterdataId", instance.getArcPipelineparameterdataId());
        writeNonNullBoolean(generator, "batchparam", instance.getBatchparam());
        writeNonBlankField(generator, "csvvalues", instance.getCsvvalues());
        writeNonBlankField(generator, "csvvalues_selected", instance.getCsvvalues_selected());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonNullBoolean(generator, "editable", instance.getEditable());
        writeNonNullBoolean(generator, "multiplevalues", instance.getMultiplevalues());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "schemalink", instance.getSchemalink());
    }
}

