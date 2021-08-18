package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrassessordata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatMrassessordataDeserializer<T extends XnatMrassessordata> extends XnatImageassessordataDeserializer<T> {
    private static final long serialVersionUID = 6488839129524691360L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrassessordataDeserializer() {
        this((Class<T>) XnatMrassessordata.class);
    }

    protected XnatMrassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

