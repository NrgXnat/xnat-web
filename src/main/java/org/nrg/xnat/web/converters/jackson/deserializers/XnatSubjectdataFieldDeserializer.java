package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectdataField;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSubjectdataFieldDeserializer<T extends XnatSubjectdataField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5426834267937666880L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectdataFieldDeserializer() {
        this((Class<T>) XnatSubjectdataField.class);
    }

    protected XnatSubjectdataFieldDeserializer(final Class<T> clazz) {
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
            case "xnatSubjectdataFieldId":
                instance.setXnatSubjectdataFieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

