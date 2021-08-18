package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSecurity;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatSecurityDeserializer<T extends XdatSecurity> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4389160022125301720L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatSecurityDeserializer() {
        this((Class<T>) XdatSecurity.class);
    }

    protected XdatSecurityDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "actions_action":
                // TODO: Handle the "actions_action" property here: java.util.ArrayList
                break;
            case "elementSecuritySet_elementSecurity":
                // TODO: Handle the "elementSecuritySet_elementSecurity" property here: java.util.ArrayList
                break;
            case "groups_group":
                // TODO: Handle the "groups_group" property here: java.util.ArrayList
                break;
            case "infolist_info":
                // TODO: Handle the "infolist_info" property here: org.nrg.xdat.om.XdatInfoentryI
                break;
            case "newslist_news":
                // TODO: Handle the "newslist_news" property here: org.nrg.xdat.om.XdatNewsentryI
                break;
            case "requireLogin":
                instance.setRequireLogin(parser.getBooleanValue());
                break;
            case "roles_role":
                // TODO: Handle the "roles_role" property here: java.util.ArrayList
                break;
            case "system":
                instance.setSystem(parser.getText());
                break;
            case "users_user":
                // TODO: Handle the "users_user" property here: java.util.ArrayList
                break;
            case "xdatSecurityId":
                instance.setXdatSecurityId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

