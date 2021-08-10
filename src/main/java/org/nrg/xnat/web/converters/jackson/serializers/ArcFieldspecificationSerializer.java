package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcFieldspecification;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcFieldspecificationSerializer<T extends ArcFieldspecification> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6768888761203560301L;

    @SuppressWarnings("unchecked")
    public ArcFieldspecificationSerializer() {
        this((Class<T>) ArcFieldspecification.class);
    }

    protected ArcFieldspecificationSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "arcFieldspecificationId" property here: Integer
        // TODO: Write out the "fieldspecification" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
    }
}

