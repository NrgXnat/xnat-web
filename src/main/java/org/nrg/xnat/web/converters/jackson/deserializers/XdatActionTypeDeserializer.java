package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatActionType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatActionTypeDeserializer<T extends XdatActionType> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1211311502910217512L;

    @SuppressWarnings("unchecked")
    public XdatActionTypeDeserializer() {
        this((Class<T>) XdatActionType.class);
    }

    public XdatActionTypeDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "actionName":
                // TODO: Handle the "actionName" property here: String
                break;
            case "displayName":
                // TODO: Handle the "displayName" property here: String
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

