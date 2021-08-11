package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectdataField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectdataFieldDeserializer<T extends XnatSubjectdataField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8946815084822379253L;

    @SuppressWarnings("unchecked")
    public XnatSubjectdataFieldDeserializer() {
        this((Class<T>) XnatSubjectdataField.class);
    }

    public XnatSubjectdataFieldDeserializer(final Class<T> clazz) {
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
            case "xnatSubjectdataFieldId":
                // TODO: Handle the "xnatSubjectdataFieldId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

