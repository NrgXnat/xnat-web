package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatUserSerializer<T extends XdatUser> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 985812685408187862L;

    @SuppressWarnings("unchecked")
    public XdatUserSerializer() {
        this((Class<T>) XdatUser.class);
    }

    protected XdatUserSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "assignedRoles_assignedRole" property here: java.util.ArrayList
        // TODO: Write out the "elementAccess" property here: java.util.ArrayList
        // TODO: Write out the "email" property here: String
        // TODO: Write out the "enabled" property here: Boolean
        // TODO: Write out the "firstname" property here: String
        // TODO: Write out the "groups_groupid" property here: java.util.ArrayList
        // TODO: Write out the "lastname" property here: String
        // TODO: Write out the "login" property here: String
        // TODO: Write out the "primaryPassword" property here: String
        // TODO: Write out the "primaryPassword_encrypt" property here: Boolean
        // TODO: Write out the "quarantinePath" property here: String
        // TODO: Write out the "salt" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "verified" property here: Boolean
        // TODO: Write out the "xdatUserId" property here: Integer
    }
}

