package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatNewsentry;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatNewsentryDeserializer<T extends XdatNewsentry> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6501882288173422789L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatNewsentryDeserializer() {
        this((Class<T>) XdatNewsentry.class);
    }

    protected XdatNewsentryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "date":
                // TODO: Handle the "date" property here: Object
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "link":
                instance.setLink(parser.getText());
                break;
            case "title":
                instance.setTitle(parser.getText());
                break;
            case "xdatNewsentryId":
                instance.setXdatNewsentryId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

