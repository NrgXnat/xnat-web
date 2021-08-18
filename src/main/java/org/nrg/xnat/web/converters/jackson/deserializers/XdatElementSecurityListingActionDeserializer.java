package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurityListingAction;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatElementSecurityListingActionDeserializer<T extends XdatElementSecurityListingAction> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1845975813736538606L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementSecurityListingActionDeserializer() {
        this((Class<T>) XdatElementSecurityListingAction.class);
    }

    protected XdatElementSecurityListingActionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "displayName":
                instance.setDisplayName(parser.getText());
                break;
            case "elementActionName":
                instance.setElementActionName(parser.getText());
                break;
            case "image":
                instance.setImage(parser.getText());
                break;
            case "parameterstring":
                instance.setParameterstring(parser.getText());
                break;
            case "popup":
                instance.setPopup(parser.getText());
                break;
            case "secureaccess":
                instance.setSecureaccess(parser.getText());
                break;
            case "sequence":
                instance.setSequence(parser.getIntValue());
                break;
            case "xdatElementSecurityListingActionId":
                instance.setXdatElementSecurityListingActionId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

