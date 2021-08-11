package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandataImpedance;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegscandataImpedanceSerializer<T extends XnatEegscandataImpedance> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2201143874396089708L;

    @SuppressWarnings("unchecked")
    public XnatEegscandataImpedanceSerializer() {
        this((Class<T>) XnatEegscandataImpedance.class);
    }

    protected XnatEegscandataImpedanceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "value" property here: String
        // TODO: Write out the "xnatEegscandataImpedanceId" property here: Integer
    }
}

