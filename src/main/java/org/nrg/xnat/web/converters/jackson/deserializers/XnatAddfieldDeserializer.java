package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAddfield;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatAddfieldDeserializer<T extends XnatAddfield> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7798130088623762990L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAddfieldDeserializer() {
        this((Class<T>) XnatAddfield.class);
    }

    protected XnatAddfieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "addfield":
                // TODO: Handle the "addfield" property here: Object
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatAddfieldId":
                instance.setXnatAddfieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

