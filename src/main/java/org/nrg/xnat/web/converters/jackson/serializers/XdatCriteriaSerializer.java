package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatCriteria;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatCriteriaSerializer<T extends XdatCriteria> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4859553987081741285L;

    @SuppressWarnings("unchecked")
    public XdatCriteriaSerializer() {
        this((Class<T>) XdatCriteria.class);
    }

    protected XdatCriteriaSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "comparisonType" property here: String
        // TODO: Write out the "customSearch" property here: String
        // TODO: Write out the "overrideValueFormatting" property here: Boolean
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "schemaField" property here: String
        // TODO: Write out the "value" property here: String
        // TODO: Write out the "xdatCriteriaId" property here: Integer
    }
}

