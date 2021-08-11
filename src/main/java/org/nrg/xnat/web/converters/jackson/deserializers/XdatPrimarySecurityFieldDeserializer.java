package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatPrimarySecurityField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatPrimarySecurityFieldDeserializer<T extends XdatPrimarySecurityField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6168569784760753732L;

    @SuppressWarnings("unchecked")
    public XdatPrimarySecurityFieldDeserializer() {
        this((Class<T>) XdatPrimarySecurityField.class);
    }

    public XdatPrimarySecurityFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "primarySecurityField":
                // TODO: Handle the "primarySecurityField" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xdatPrimarySecurityFieldId":
                // TODO: Handle the "xdatPrimarySecurityFieldId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

