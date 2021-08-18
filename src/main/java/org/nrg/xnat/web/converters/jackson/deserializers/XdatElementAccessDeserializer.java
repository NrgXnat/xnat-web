package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementAccess;
import org.nrg.xdat.om.XdatElementAccessSecureIp;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatElementAccessDeserializer<T extends XdatElementAccess> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1300556169889589133L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementAccessDeserializer() {
        this((Class<T>) XdatElementAccess.class);
    }

    protected XdatElementAccessDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "xdatElementAccessId":
                instance.setXdatElementAccessId(parser.getIntValue());
                break;
            case "elementName":
                instance.setElementName(parser.getText());
                break;
            case "secondaryPassword":
                instance.setSecondaryPassword(parser.getText());
                break;
            case "secondaryPasswordEncrypt":
                instance.setSecondaryPassword_encrypt(parser.getBooleanValue());
                break;
            case "secureIp":
                final XdatElementAccessSecureIp secureIp = new XdatElementAccessSecureIp();
                final String value = parser.getText();
                secureIp.setSecureIp(value);
                try {
                    instance.setSecureIp(secureIp);
                } catch (Exception e) {
                    log.error("An error occurred trying to set the secure IP value {} for an XdatElementAccess instance", value, e);
                }
                break;
            case "permissionsAllowSet":
                log.warn("This is not currently implemented and probably doesn't need to be");
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
