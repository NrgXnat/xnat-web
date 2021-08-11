package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcessstep;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ProvProcessstepSerializer<T extends ProvProcessstep> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1475175171865130132L;

    @SuppressWarnings("unchecked")
    public ProvProcessstepSerializer() {
        this((Class<T>) ProvProcessstep.class);
    }

    protected ProvProcessstepSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "compiler" property here: String
        // TODO: Write out the "compiler_version" property here: String
        // TODO: Write out the "library" property here: java.util.List
        // TODO: Write out the "machine" property here: String
        // TODO: Write out the "platform" property here: String
        // TODO: Write out the "platform_version" property here: String
        // TODO: Write out the "program" property here: String
        // TODO: Write out the "program_arguments" property here: String
        // TODO: Write out the "program_version" property here: String
        // TODO: Write out the "provProcessstepId" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "timestamp" property here: Object
    }
}

