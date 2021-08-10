package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcscandataField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatQcscandataFieldSerializer<T extends XnatQcscandataField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6894377256071011485L;

    @SuppressWarnings("unchecked")
    public XnatQcscandataFieldSerializer() {
        this((Class<T>) XnatQcscandataField.class);
    }

    protected XnatQcscandataFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "field" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatQcscandataFieldId" property here: Integer
    }
}

