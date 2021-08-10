package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUserLogin;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatUserLoginDeserializer<T extends XdatUserLogin> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1452328633147202032L;

    @SuppressWarnings("unchecked")
    public XdatUserLoginDeserializer() {
        this((Class<T>) XdatUserLogin.class);
    }

    public XdatUserLoginDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "ipAddress":
                // TODO: Handle the "ipAddress" property here: String
                break;
            case "loginDate":
                // TODO: Handle the "loginDate" property here: Object
                break;
            case "nodeId":
                // TODO: Handle the "nodeId" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "userProperty":
                // TODO: Handle the "userProperty" property here: org.nrg.xdat.om.XdatUserI
                break;
            case "xdatUserLoginId":
                // TODO: Handle the "xdatUserLoginId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

