package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStudyprotocolSessionDeserializer<T extends XnatStudyprotocolSession> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1241742352933828261L;

    @SuppressWarnings("unchecked")
    public XnatStudyprotocolSessionDeserializer() {
        this((Class<T>) XnatStudyprotocolSession.class);
    }

    public XnatStudyprotocolSessionDeserializer(final Class<T> clazz) {
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
            case "xnatStudyprotocolSessionId":
                // TODO: Handle the "xnatStudyprotocolSessionId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

