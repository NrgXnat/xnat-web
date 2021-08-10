package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolVariable;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStudyprotocolVariableDeserializer<T extends XnatStudyprotocolVariable> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8550297095053961473L;

    @SuppressWarnings("unchecked")
    public XnatStudyprotocolVariableDeserializer() {
        this((Class<T>) XnatStudyprotocolVariable.class);
    }

    public XnatStudyprotocolVariableDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatStudyprotocolVariableId":
                // TODO: Handle the "xnatStudyprotocolVariableId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

