package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkAbstractexecutionenvironment;

import java.io.IOException;

@Slf4j
public abstract class WrkAbstractexecutionenvironmentSerializer<T extends WrkAbstractexecutionenvironment> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4836434967737132001L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkAbstractexecutionenvironmentSerializer() {
        this((Class<T>) WrkAbstractexecutionenvironment.class);
    }

    protected WrkAbstractexecutionenvironmentSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "wrkAbstractexecutionenvironmentId", instance.getWrkAbstractexecutionenvironmentId());
    }
}

