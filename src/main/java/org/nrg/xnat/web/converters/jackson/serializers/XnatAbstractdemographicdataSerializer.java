package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractdemographicdata;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractdemographicdataSerializer<T extends XnatAbstractdemographicdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6682960780894782111L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAbstractdemographicdataSerializer() {
        this((Class<T>) XnatAbstractdemographicdata.class);
    }

    protected XnatAbstractdemographicdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "xnatAbstractdemographicdataId", instance.getXnatAbstractdemographicdataId());
    }
}

