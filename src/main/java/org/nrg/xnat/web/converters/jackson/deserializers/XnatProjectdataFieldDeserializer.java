package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdataField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatProjectdataFieldDeserializer<T extends XnatProjectdataField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5952913340845275996L;

    @SuppressWarnings("unchecked")
    public XnatProjectdataFieldDeserializer() {
        this((Class<T>) XnatProjectdataField.class);
    }

    public XnatProjectdataFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "field":
                // TODO: Handle the "field" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatProjectdataFieldId":
                // TODO: Handle the "xnatProjectdataFieldId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

