package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatUserDeserializer<T extends XdatUser> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1599411742748537310L;

    @SuppressWarnings("unchecked")
    public XdatUserDeserializer() {
        this((Class<T>) XdatUser.class);
    }

    public XdatUserDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
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
                // TODO: Handle the "email" property here: String
                break;
            case "enabled":
                // TODO: Handle the "enabled" property here: Boolean
                break;
            case "firstname":
                // TODO: Handle the "firstname" property here: String
                break;
            case "groups_groupid":
                // TODO: Handle the "groups_groupid" property here: java.util.ArrayList
                break;
            case "lastname":
                // TODO: Handle the "lastname" property here: String
                break;
            case "login":
                // TODO: Handle the "login" property here: String
                break;
            case "primaryPassword":
                // TODO: Handle the "primaryPassword" property here: String
                break;
            case "primaryPassword_encrypt":
                // TODO: Handle the "primaryPassword_encrypt" property here: Boolean
                break;
            case "quarantinePath":
                // TODO: Handle the "quarantinePath" property here: String
                break;
            case "salt":
                // TODO: Handle the "salt" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "verified":
                // TODO: Handle the "verified" property here: Boolean
                break;
            case "xdatUserId":
                // TODO: Handle the "xdatUserId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

