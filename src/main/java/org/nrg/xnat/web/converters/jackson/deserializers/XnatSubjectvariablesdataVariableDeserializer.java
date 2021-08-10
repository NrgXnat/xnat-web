package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdataVariable;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectvariablesdataVariableDeserializer<T extends XnatSubjectvariablesdataVariable> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -246849031018214860L;

    @SuppressWarnings("unchecked")
    public XnatSubjectvariablesdataVariableDeserializer() {
        this((Class<T>) XnatSubjectvariablesdataVariable.class);
    }

    public XnatSubjectvariablesdataVariableDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "variable":
                // TODO: Handle the "variable" property here: String
                break;
            case "xnatSubjectvariablesdataVariableId":
                // TODO: Handle the "xnatSubjectvariablesdataVariableId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

