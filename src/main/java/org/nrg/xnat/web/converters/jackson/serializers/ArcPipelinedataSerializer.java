package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPipelinedata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcPipelinedataSerializer<T extends ArcPipelinedata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5293047609755294346L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPipelinedataSerializer() {
        this((Class<T>) ArcPipelinedata.class);
    }

    protected ArcPipelinedataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "arcPipelinedataId", instance.getArcPipelinedataId());
        writeNonBlankField(generator, "customwebpage", instance.getCustomwebpage());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "displaytext", instance.getDisplaytext());
        writeNonBlankField(generator, "location", instance.getLocation());
        writeNonBlankField(generator, "name", instance.getName());
        // TODO: Write out the "parameters_parameter" property here: java.util.List
    }
}

