package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUserLogin;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatUserLoginDeserializer<T extends XdatUserLogin> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7054107268711370385L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatUserLoginDeserializer() {
        this((Class<T>) XdatUserLogin.class);
    }

    protected XdatUserLoginDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "ipAddress":
                instance.setIpAddress(parser.getText());
                break;
            case "loginDate":
                // TODO: Handle the "loginDate" property here: Object
                break;
            case "nodeId":
                instance.setNodeId(parser.getText());
                break;
            case "userProperty":
                // TODO: Handle the "userProperty" property here: org.nrg.xdat.om.XdatUserI
                break;
            case "xdatUserLoginId":
                instance.setXdatUserLoginId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

