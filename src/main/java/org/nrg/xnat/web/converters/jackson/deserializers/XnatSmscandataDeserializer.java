package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSmscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSmscandataDeserializer<T extends XnatSmscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 3220096316150014198L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSmscandataDeserializer() {
        this((Class<T>) XnatSmscandata.class);
    }

    protected XnatSmscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

