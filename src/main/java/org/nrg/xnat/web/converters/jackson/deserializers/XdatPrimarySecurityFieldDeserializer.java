package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatPrimarySecurityField;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatPrimarySecurityFieldDeserializer<T extends XdatPrimarySecurityField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8832554016863154663L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatPrimarySecurityFieldDeserializer() {
        this((Class<T>) XdatPrimarySecurityField.class);
    }

    protected XdatPrimarySecurityFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "primarySecurityField":
                instance.setPrimarySecurityField(parser.getText());
                break;
            case "xdatPrimarySecurityFieldId":
                instance.setXdatPrimarySecurityFieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

