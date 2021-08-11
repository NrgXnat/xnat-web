package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatDcmentry;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CatDcmentrySerializer<T extends CatDcmentry> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8802371581776995130L;

    @SuppressWarnings("unchecked")
    public CatDcmentrySerializer() {
        this((Class<T>) CatDcmentry.class);
    }

    protected CatDcmentrySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "entry" property here: org.nrg.xdat.om.CatEntry
        // TODO: Write out the "instancenumber" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
    }
}

