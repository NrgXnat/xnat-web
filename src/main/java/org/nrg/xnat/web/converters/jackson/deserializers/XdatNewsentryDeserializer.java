package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatNewsentry;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatNewsentryDeserializer<T extends XdatNewsentry> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1655667694369379825L;

    @SuppressWarnings("unchecked")
    public XdatNewsentryDeserializer() {
        this((Class<T>) XdatNewsentry.class);
    }

    public XdatNewsentryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "date":
                // TODO: Handle the "date" property here: Object
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "link":
                // TODO: Handle the "link" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "title":
                // TODO: Handle the "title" property here: String
                break;
            case "xdatNewsentryId":
                // TODO: Handle the "xdatNewsentryId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

