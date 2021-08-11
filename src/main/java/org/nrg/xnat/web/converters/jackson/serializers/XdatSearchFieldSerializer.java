package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearchField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatSearchFieldSerializer<T extends XdatSearchField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3737501241855325874L;

    @SuppressWarnings("unchecked")
    public XdatSearchFieldSerializer() {
        this((Class<T>) XdatSearchField.class);
    }

    protected XdatSearchFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "elementName" property here: String
        // TODO: Write out the "fieldId" property here: String
        // TODO: Write out the "header" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "sequence" property here: Integer
        // TODO: Write out the "type" property here: String
        // TODO: Write out the "value" property here: String
        // TODO: Write out the "visible" property here: Boolean
        // TODO: Write out the "xdatSearchFieldId" property here: Integer
    }
}

