package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatFieldMapping;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatFieldMappingSerializer<T extends XdatFieldMapping> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -190980813743565110L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatFieldMappingSerializer() {
        this((Class<T>) XdatFieldMapping.class);
    }

    protected XdatFieldMappingSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullBoolean(generator, "activeElement", instance.getActiveElement());
        writeNonBlankField(generator, "comparisonType", instance.getComparisonType());
        writeNonNullBoolean(generator, "createElement", instance.getCreateElement());
        writeNonNullBoolean(generator, "deleteElement", instance.getDeleteElement());
        writeNonNullBoolean(generator, "editElement", instance.getEditElement());
        writeNonBlankField(generator, "field", instance.getField());
        writeNonBlankField(generator, "fieldValue", instance.getFieldValue());
        writeNonNullBoolean(generator, "readElement", instance.getReadElement());
        writeNonNullNumber(generator, "xdatFieldMappingId", instance.getXdatFieldMappingId());
    }
}

