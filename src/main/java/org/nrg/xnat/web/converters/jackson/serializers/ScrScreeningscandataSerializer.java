package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ScrScreeningscandataSerializer<T extends ScrScreeningscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5947999598407813208L;

    @SuppressWarnings("unchecked")
    public ScrScreeningscandataSerializer() {
        this((Class<T>) ScrScreeningscandata.class);
    }

    protected ScrScreeningscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "comments" property here: String
        // TODO: Write out the "imagescanId" property here: String
        // TODO: Write out the "pass" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "scrScreeningscandataId" property here: Integer
        // TODO: Write out the "summary" property here: String
    }
}

