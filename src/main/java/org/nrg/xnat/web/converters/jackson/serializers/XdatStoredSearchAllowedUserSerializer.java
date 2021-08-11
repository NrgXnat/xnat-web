package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatStoredSearchAllowedUser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatStoredSearchAllowedUserSerializer<T extends XdatStoredSearchAllowedUser> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7350017635542812276L;

    @SuppressWarnings("unchecked")
    public XdatStoredSearchAllowedUserSerializer() {
        this((Class<T>) XdatStoredSearchAllowedUser.class);
    }

    protected XdatStoredSearchAllowedUserSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "login" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xdatStoredSearchAllowedUserId" property here: Integer
    }
}

