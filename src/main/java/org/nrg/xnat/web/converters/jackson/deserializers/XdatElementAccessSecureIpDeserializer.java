package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementAccessSecureIp;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementAccessSecureIpDeserializer<T extends XdatElementAccessSecureIp> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1410886634731892632L;

    @SuppressWarnings("unchecked")
    public XdatElementAccessSecureIpDeserializer() {
        this((Class<T>) XdatElementAccessSecureIp.class);
    }

    public XdatElementAccessSecureIpDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "secureIp":
                // TODO: Handle the "secureIp" property here: String
                break;
            case "xdatElementAccessSecureIpId":
                // TODO: Handle the "xdatElementAccessSecureIpId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

