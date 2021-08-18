package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheckCondition;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ValProtocoldataScanCheckConditionSerializer<T extends ValProtocoldataScanCheckCondition> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8679795335327991672L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataScanCheckConditionSerializer() {
        this((Class<T>) ValProtocoldataScanCheckCondition.class);
    }

    protected ValProtocoldataScanCheckConditionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "diagnosis", instance.getDiagnosis());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "status", instance.getStatus());
        writeNonNullNumber(generator, "valProtocoldataScanCheckConditionId", instance.getValProtocoldataScanCheckConditionId());
        writeNonBlankField(generator, "verified", instance.getVerified());
        writeNonBlankField(generator, "xmlpath", instance.getXmlpath());
    }
}

