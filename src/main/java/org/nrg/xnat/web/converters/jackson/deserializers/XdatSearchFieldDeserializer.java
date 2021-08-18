package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearchField;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatSearchFieldDeserializer<T extends XdatSearchField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 491983132027459299L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatSearchFieldDeserializer() {
        this((Class<T>) XdatSearchField.class);
    }

    protected XdatSearchFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "elementName":
                instance.setElementName(parser.getText());
                break;
            case "fieldId":
                instance.setFieldId(parser.getText());
                break;
            case "header":
                instance.setHeader(parser.getText());
                break;
            case "sequence":
                instance.setSequence(parser.getIntValue());
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            case "value":
                instance.setValue(parser.getText());
                break;
            case "visible":
                instance.setVisible(parser.getBooleanValue());
                break;
            case "xdatSearchFieldId":
                instance.setXdatSearchFieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

