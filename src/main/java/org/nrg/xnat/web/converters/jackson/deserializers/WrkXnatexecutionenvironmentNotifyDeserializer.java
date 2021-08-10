package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentNotify;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkXnatexecutionenvironmentNotifyDeserializer<T extends WrkXnatexecutionenvironmentNotify> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8902210953172984553L;

    @SuppressWarnings("unchecked")
    public WrkXnatexecutionenvironmentNotifyDeserializer() {
        this((Class<T>) WrkXnatexecutionenvironmentNotify.class);
    }

    public WrkXnatexecutionenvironmentNotifyDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "notify":
                // TODO: Handle the "notify" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "wrkXnatexecutionenvironmentNotifyId":
                // TODO: Handle the "wrkXnatexecutionenvironmentNotifyId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

