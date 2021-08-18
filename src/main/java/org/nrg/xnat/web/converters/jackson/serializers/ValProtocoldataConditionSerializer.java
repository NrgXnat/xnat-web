package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataCondition;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ValProtocoldataConditionSerializer<T extends ValProtocoldataCondition> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -4540177042111749018L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataConditionSerializer() {
        this((Class<T>) ValProtocoldataCondition.class);
    }

    protected ValProtocoldataConditionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "diagnosis", instance.getDiagnosis());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "status", instance.getStatus());
        writeNonNullNumber(generator, "valProtocoldataConditionId", instance.getValProtocoldataConditionId());
        writeNonBlankField(generator, "verified", instance.getVerified());
        writeNonBlankField(generator, "xmlpath", instance.getXmlpath());
    }
}

