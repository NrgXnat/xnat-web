package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearchField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatSearchFieldDeserializer<T extends XdatSearchField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5510184964795467227L;

    @SuppressWarnings("unchecked")
    public XdatSearchFieldDeserializer() {
        this((Class<T>) XdatSearchField.class);
    }

    public XdatSearchFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "elementName":
                // TODO: Handle the "elementName" property here: String
                break;
            case "fieldId":
                // TODO: Handle the "fieldId" property here: String
                break;
            case "header":
                // TODO: Handle the "header" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sequence":
                // TODO: Handle the "sequence" property here: Integer
                break;
            case "type":
                // TODO: Handle the "type" property here: String
                break;
            case "value":
                // TODO: Handle the "value" property here: String
                break;
            case "visible":
                // TODO: Handle the "visible" property here: Boolean
                break;
            case "xdatSearchFieldId":
                // TODO: Handle the "xdatSearchFieldId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

