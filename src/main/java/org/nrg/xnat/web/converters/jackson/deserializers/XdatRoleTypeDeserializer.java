package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatRoleType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatRoleTypeDeserializer<T extends XdatRoleType> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1547505670902361314L;

    @SuppressWarnings("unchecked")
    public XdatRoleTypeDeserializer() {
        this((Class<T>) XdatRoleType.class);
    }

    public XdatRoleTypeDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "allowedActions_allowedAction":
                // TODO: Handle the "allowedActions_allowedAction" property here: java.util.ArrayList
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "roleName":
                // TODO: Handle the "roleName" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sequence":
                // TODO: Handle the "sequence" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

