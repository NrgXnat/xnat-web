package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcPropertySerializer<T extends ArcProperty> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6234231611694212867L;

    @SuppressWarnings("unchecked")
    public ArcPropertySerializer() {
        this((Class<T>) ArcProperty.class);
    }

    protected ArcPropertySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "arcPropertyId" property here: Integer
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "property" property here: String
        // TODO: Write out the "schemaElementName" property here: String
    }
}

