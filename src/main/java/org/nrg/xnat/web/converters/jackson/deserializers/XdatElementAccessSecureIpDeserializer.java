package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementAccessSecureIp;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatElementAccessSecureIpDeserializer<T extends XdatElementAccessSecureIp> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1997954333305301841L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementAccessSecureIpDeserializer() {
        this((Class<T>) XdatElementAccessSecureIp.class);
    }

    protected XdatElementAccessSecureIpDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "secureIp":
                instance.setSecureIp(parser.getText());
                break;
            case "xdatElementAccessSecureIpId":
                instance.setXdatElementAccessSecureIpId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

