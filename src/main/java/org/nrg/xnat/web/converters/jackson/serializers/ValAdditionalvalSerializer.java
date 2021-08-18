package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValAdditionalval;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ValAdditionalvalSerializer<T extends ValAdditionalval> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8295409336566058830L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValAdditionalvalSerializer() {
        this((Class<T>) ValAdditionalval.class);
    }

    protected ValAdditionalvalSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "valAdditionalvalId", instance.getValAdditionalvalId());
    }
}

