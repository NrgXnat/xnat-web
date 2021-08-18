package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcess;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ProvProcessSerializer<T extends ProvProcess> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -7421743901131216611L;

    @SuppressWarnings({"unchecked", "unused"})
    public ProvProcessSerializer() {
        this((Class<T>) ProvProcess.class);
    }

    protected ProvProcessSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "processstep" property here: java.util.List
        writeNonNullNumber(generator, "provProcessId", instance.getProvProcessId());
    }
}

