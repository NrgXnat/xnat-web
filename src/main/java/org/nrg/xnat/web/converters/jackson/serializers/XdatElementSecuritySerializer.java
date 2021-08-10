package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurity;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementSecuritySerializer<T extends XdatElementSecurity> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -435051657406004156L;

    @SuppressWarnings("unchecked")
    public XdatElementSecuritySerializer() {
        this((Class<T>) XdatElementSecurity.class);
    }

    protected XdatElementSecuritySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "accessible" property here: Boolean
        // TODO: Write out the "browse" property here: Boolean
        // TODO: Write out the "category" property here: String
        // TODO: Write out the "code" property here: String
        // TODO: Write out the "elementActions_elementAction" property here: java.util.ArrayList
        // TODO: Write out the "elementName" property here: String
        // TODO: Write out the "listingActions_listingAction" property here: java.util.ArrayList
        // TODO: Write out the "plural" property here: String
        // TODO: Write out the "preLoad" property here: Boolean
        // TODO: Write out the "primarySecurityFields_primarySecurityField" property here: java.util.ArrayList
        // TODO: Write out the "quarantine" property here: Boolean
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "searchable" property here: Boolean
        // TODO: Write out the "secondaryPassword" property here: Boolean
        // TODO: Write out the "secure" property here: Boolean
        // TODO: Write out the "secureCreate" property here: Boolean
        // TODO: Write out the "secureDelete" property here: Boolean
        // TODO: Write out the "secureEdit" property here: Boolean
        // TODO: Write out the "secureIp" property here: Boolean
        // TODO: Write out the "secureRead" property here: Boolean
        // TODO: Write out the "sequence" property here: Integer
        // TODO: Write out the "singular" property here: String
        // TODO: Write out the "usage" property here: String
    }
}

