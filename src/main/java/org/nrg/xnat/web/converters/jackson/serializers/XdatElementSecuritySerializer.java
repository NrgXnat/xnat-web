package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurity;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatElementSecuritySerializer<T extends XdatElementSecurity> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -435051657406004156L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementSecuritySerializer() {
        this((Class<T>) XdatElementSecurity.class);
    }

    protected XdatElementSecuritySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullBoolean(generator, "accessible", instance.getAccessible());
        writeNonNullBoolean(generator, "browse", instance.getBrowse());
        writeNonBlankField(generator, "category", instance.getCategory());
        writeNonBlankField(generator, "code", instance.getCode());
        // TODO: Write out the "elementActions_elementAction" property here: java.util.ArrayList
        writeNonBlankField(generator, "elementName", instance.getElementName());
        // TODO: Write out the "listingActions_listingAction" property here: java.util.ArrayList
        writeNonBlankField(generator, "plural", instance.getPlural());
        writeNonNullBoolean(generator, "preLoad", instance.getPreLoad());
        // TODO: Write out the "primarySecurityFields_primarySecurityField" property here: java.util.ArrayList
        writeNonNullBoolean(generator, "quarantine", instance.getQuarantine());
        writeNonNullBoolean(generator, "searchable", instance.getSearchable());
        writeNonNullBoolean(generator, "secondaryPassword", instance.getSecondaryPassword());
        writeNonNullBoolean(generator, "secure", instance.getSecure());
        writeNonNullBoolean(generator, "secureCreate", instance.getSecureCreate());
        writeNonNullBoolean(generator, "secureDelete", instance.getSecureDelete());
        writeNonNullBoolean(generator, "secureEdit", instance.getSecureEdit());
        writeNonNullBoolean(generator, "secureIp", instance.getSecureIp());
        writeNonNullBoolean(generator, "secureRead", instance.getSecureRead());
        writeNonNullNumber(generator, "sequence", instance.getSequence());
        writeNonBlankField(generator, "singular", instance.getSingular());
        writeNonBlankField(generator, "usage", instance.getUsage());
    }
}

