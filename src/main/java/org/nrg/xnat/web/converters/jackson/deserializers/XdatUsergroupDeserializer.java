package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUsergroup;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatUsergroupDeserializer<T extends XdatUsergroup> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7044647274192011368L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatUsergroupDeserializer() {
        this((Class<T>) XdatUsergroup.class);
    }

    protected XdatUsergroupDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "displayname":
                instance.setDisplayname(parser.getText());
                break;
            case "elementAccess":
                // TODO: Handle the "elementAccess" property here: java.util.ArrayList
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "tag":
                instance.setTag(parser.getText());
                break;
            case "xdatUsergroupId":
                instance.setXdatUsergroupId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

