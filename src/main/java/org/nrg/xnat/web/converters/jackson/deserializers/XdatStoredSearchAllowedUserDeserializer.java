package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatStoredSearchAllowedUser;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatStoredSearchAllowedUserDeserializer<T extends XdatStoredSearchAllowedUser> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8750036308722342769L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatStoredSearchAllowedUserDeserializer() {
        this((Class<T>) XdatStoredSearchAllowedUser.class);
    }

    protected XdatStoredSearchAllowedUserDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "login":
                instance.setLogin(parser.getText());
                break;
            case "xdatStoredSearchAllowedUserId":
                instance.setXdatStoredSearchAllowedUserId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

