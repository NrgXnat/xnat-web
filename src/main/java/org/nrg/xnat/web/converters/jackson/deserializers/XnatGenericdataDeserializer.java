package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGenericdata;

import java.io.IOException;

@Slf4j
public abstract class XnatGenericdataDeserializer<T extends XnatGenericdata> extends XnatExperimentdataDeserializer<T> {
    private static final long serialVersionUID = 6463017862836042462L;

    protected XnatGenericdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

