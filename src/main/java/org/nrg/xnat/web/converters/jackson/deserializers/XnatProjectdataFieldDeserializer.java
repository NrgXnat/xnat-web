package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdataField;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatProjectdataFieldDeserializer<T extends XnatProjectdataField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1636308284806355398L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatProjectdataFieldDeserializer() {
        this((Class<T>) XnatProjectdataField.class);
    }

    protected XnatProjectdataFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "field":
                instance.setField(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatProjectdataFieldId":
                instance.setXnatProjectdataFieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

