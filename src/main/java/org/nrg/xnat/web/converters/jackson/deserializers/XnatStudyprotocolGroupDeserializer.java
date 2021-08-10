package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolGroup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStudyprotocolGroupDeserializer<T extends XnatStudyprotocolGroup> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3248296434844428830L;

    @SuppressWarnings("unchecked")
    public XnatStudyprotocolGroupDeserializer() {
        this((Class<T>) XnatStudyprotocolGroup.class);
    }

    public XnatStudyprotocolGroupDeserializer(final Class<T> clazz) {
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
            case "xnatStudyprotocolGroupId":
                // TODO: Handle the "xnatStudyprotocolGroupId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

