package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.PipePipelinedetailsElement;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class PipePipelinedetailsElementSerializer<T extends PipePipelinedetailsElement> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6078461829016488885L;

    @SuppressWarnings({"unchecked", "unused"})
    public PipePipelinedetailsElementSerializer() {
        this((Class<T>) PipePipelinedetailsElement.class);
    }

    protected PipePipelinedetailsElementSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "element", instance.getElement());
        writeNonNullNumber(generator, "pipePipelinedetailsElementId", instance.getPipePipelinedetailsElementId());
    }
}

