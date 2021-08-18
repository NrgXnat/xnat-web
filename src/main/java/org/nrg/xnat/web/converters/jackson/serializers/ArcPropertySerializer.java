package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProperty;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcPropertySerializer<T extends ArcProperty> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6234231611694212867L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPropertySerializer() {
        this((Class<T>) ArcProperty.class);
    }

    protected ArcPropertySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "arcPropertyId", instance.getArcPropertyId());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "property", instance.getProperty());
    }
}

