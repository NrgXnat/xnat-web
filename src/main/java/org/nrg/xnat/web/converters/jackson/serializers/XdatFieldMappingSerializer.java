package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatFieldMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatFieldMappingSerializer<T extends XdatFieldMapping> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -190980813743565110L;

    @SuppressWarnings("unchecked")
    public XdatFieldMappingSerializer() {
        this((Class<T>) XdatFieldMapping.class);
    }

    protected XdatFieldMappingSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "activeElement" property here: Boolean
        // TODO: Write out the "comparisonType" property here: String
        // TODO: Write out the "createElement" property here: Boolean
        // TODO: Write out the "deleteElement" property here: Boolean
        // TODO: Write out the "editElement" property here: Boolean
        // TODO: Write out the "field" property here: String
        // TODO: Write out the "fieldValue" property here: String
        // TODO: Write out the "readElement" property here: Boolean
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xdatFieldMappingId" property here: Integer
    }
}

