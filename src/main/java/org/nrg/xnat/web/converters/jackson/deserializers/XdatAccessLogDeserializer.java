package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatAccessLog;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatAccessLogDeserializer<T extends XdatAccessLog> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -829781479386032747L;

    @SuppressWarnings("unchecked")
    public XdatAccessLogDeserializer() {
        this((Class<T>) XdatAccessLog.class);
    }

    public XdatAccessLogDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "accessDate":
                // TODO: Handle the "accessDate" property here: Object
                break;
            case "login":
                // TODO: Handle the "login" property here: String
                break;
            case "method":
                // TODO: Handle the "method" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xdatAccessLogId":
                // TODO: Handle the "xdatAccessLogId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

