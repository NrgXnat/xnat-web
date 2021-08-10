package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSecurity;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatSecuritySerializer<T extends XdatSecurity> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5661757594455166842L;

    @SuppressWarnings("unchecked")
    public XdatSecuritySerializer() {
        this((Class<T>) XdatSecurity.class);
    }

    protected XdatSecuritySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "actions_action" property here: java.util.ArrayList
        // TODO: Write out the "elementSecuritySet_elementSecurity" property here: java.util.ArrayList
        // TODO: Write out the "groups_group" property here: java.util.ArrayList
        // TODO: Write out the "infolist_info" property here: org.nrg.xdat.om.XdatInfoentryI
        // TODO: Write out the "newslist_news" property here: org.nrg.xdat.om.XdatNewsentryI
        // TODO: Write out the "requireLogin" property here: Boolean
        // TODO: Write out the "roles_role" property here: java.util.ArrayList
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "system" property here: String
        // TODO: Write out the "users_user" property here: java.util.ArrayList
        // TODO: Write out the "xdatSecurityId" property here: Integer
    }
}

