package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkAbstractexecutionenvironment;

import java.io.IOException;

@Slf4j
public abstract class WrkAbstractexecutionenvironmentDeserializer<T extends WrkAbstractexecutionenvironment> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6293448696425633255L;

    protected WrkAbstractexecutionenvironmentDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        if ("wrkAbstractexecutionenvironmentId".equals(field)) {
            instance.setWrkAbstractexecutionenvironmentId(parser.getIntValue());
        } else {
            super.handleField(instance, field, parser, context);
        }
    }
}

