package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatCriteria;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatCriteriaSerializer<T extends XdatCriteria> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4859553987081741285L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatCriteriaSerializer() {
        this((Class<T>) XdatCriteria.class);
    }

    protected XdatCriteriaSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "comparisonType", instance.getComparisonType());
        writeNonBlankField(generator, "customSearch", instance.getCustomSearch());
        writeNonNullBoolean(generator, "overrideValueFormatting", instance.getOverrideValueFormatting());
        writeNonBlankField(generator, "schemaField", instance.getSchemaField());
        writeNonBlankField(generator, "value", instance.getValue());
        writeNonNullNumber(generator, "xdatCriteriaId", instance.getXdatCriteriaId());
    }
}

