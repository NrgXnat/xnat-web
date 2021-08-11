package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagescandataShare;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatImagescandataShareDeserializer<T extends XnatImagescandataShare> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8457295003564930527L;

    @SuppressWarnings("unchecked")
    public XnatImagescandataShareDeserializer() {
        this((Class<T>) XnatImagescandataShare.class);
    }

    public XnatImagescandataShareDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "label":
                // TODO: Handle the "label" property here: String
                break;
            case "project":
                // TODO: Handle the "project" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "share":
                // TODO: Handle the "share" property here: String
                break;
            case "xnatImagescandataShareId":
                // TODO: Handle the "xnatImagescandataShareId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

