package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementActionType;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatElementActionTypeDeserializer<T extends XdatElementActionType> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2453258674571012076L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementActionTypeDeserializer() {
        this((Class<T>) XdatElementActionType.class);
    }

    protected XdatElementActionTypeDeserializer(final Class<T> clazz) {
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
            case "securefeature":
                instance.setSecurefeature(parser.getText());
                break;
            case "sequence":
                instance.setSequence(parser.getIntValue());
                break;
            case "xdatElementActionTypeId":
                instance.setXdatElementActionTypeId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

