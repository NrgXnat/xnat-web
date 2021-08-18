package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUser;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatUserDeserializer<T extends XdatUser> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6125623148036289057L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatUserDeserializer() {
        this((Class<T>) XdatUser.class);
    }

    protected XdatUserDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "assignedRoles_assignedRole":
                // TODO: Handle the "assignedRoles_assignedRole" property here: java.util.ArrayList
                break;
            case "elementAccess":
                // TODO: Handle the "elementAccess" property here: java.util.ArrayList
                break;
            case "email":
                instance.setEmail(parser.getText());
                break;
            case "enabled":
                instance.setEnabled(parser.getBooleanValue());
                break;
            case "firstname":
                instance.setFirstname(parser.getText());
                break;
            case "groups_groupid":
                // TODO: Handle the "groups_groupid" property here: java.util.ArrayList
                break;
            case "lastname":
                instance.setLastname(parser.getText());
                break;
            case "login":
                instance.setLogin(parser.getText());
                break;
            case "primaryPassword":
                instance.setPrimaryPassword(parser.getText());
                break;
            case "primaryPassword_encrypt":
                instance.setPrimaryPassword_encrypt(parser.getBooleanValue());
                break;
            case "quarantinePath":
                instance.setQuarantinePath(parser.getText());
                break;
            case "salt":
                instance.setSalt(parser.getText());
                break;
            case "verified":
                instance.setVerified(parser.getBooleanValue());
                break;
            case "xdatUserId":
                instance.setXdatUserId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

