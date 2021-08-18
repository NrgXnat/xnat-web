package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUser;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatUserSerializer<T extends XdatUser> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 985812685408187862L;

    @SuppressWarnings({"unchecked", "unused"})
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
        writeNonBlankField(generator, "email", instance.getEmail());
        writeNonNullBoolean(generator, "enabled", instance.getEnabled());
        writeNonBlankField(generator, "firstname", instance.getFirstname());
        // TODO: Write out the "groups_groupid" property here: java.util.ArrayList
        writeNonBlankField(generator, "lastname", instance.getLastname());
        writeNonBlankField(generator, "login", instance.getLogin());
        writeNonBlankField(generator, "primaryPassword", instance.getPrimaryPassword());
        writeNonNullBoolean(generator, "primaryPassword_encrypt", instance.getPrimaryPassword_encrypt());
        writeNonBlankField(generator, "quarantinePath", instance.getQuarantinePath());
        writeNonBlankField(generator, "salt", instance.getSalt());
        writeNonNullBoolean(generator, "verified", instance.getVerified());
        writeNonNullNumber(generator, "xdatUserId", instance.getXdatUserId());
    }
}

