package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheckCondition;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValProtocoldataScanCheckConditionSerializer<T extends ValProtocoldataScanCheckCondition> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8679795335327991672L;

    @SuppressWarnings("unchecked")
    public ValProtocoldataScanCheckConditionSerializer() {
        this((Class<T>) ValProtocoldataScanCheckCondition.class);
    }

    protected ValProtocoldataScanCheckConditionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "diagnosis" property here: String
        // TODO: Write out the "expectedValue" property here: String
        // TODO: Write out the "foundValue" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "status" property here: String
        // TODO: Write out the "valProtocoldataScanCheckConditionId" property here: Integer
        // TODO: Write out the "verified" property here: String
        // TODO: Write out the "xmlpath" property here: String
    }
}

