package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatInfoentry;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatInfoentryDeserializer<T extends XdatInfoentry> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2014085114956627527L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatInfoentryDeserializer() {
        this((Class<T>) XdatInfoentry.class);
    }

    protected XdatInfoentryDeserializer(final Class<T> clazz) {
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
            case "xdatInfoentryId":
                instance.setXdatInfoentryId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

