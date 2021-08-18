package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetassessordata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPetassessordataDeserializer<T extends XnatPetassessordata> extends XnatImageassessordataDeserializer<T> {
    private static final long serialVersionUID = -7376879169515548939L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetassessordataDeserializer() {
        this((Class<T>) XnatPetassessordata.class);
    }

    protected XnatPetassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

