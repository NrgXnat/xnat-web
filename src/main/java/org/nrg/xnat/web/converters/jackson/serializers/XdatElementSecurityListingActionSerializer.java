package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurityListingAction;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementSecurityListingActionSerializer<T extends XdatElementSecurityListingAction> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1193274751520228776L;

    @SuppressWarnings("unchecked")
    public XdatElementSecurityListingActionSerializer() {
        this((Class<T>) XdatElementSecurityListingAction.class);
    }

    protected XdatElementSecurityListingActionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "displayName" property here: String
        // TODO: Write out the "elementActionName" property here: String
        // TODO: Write out the "image" property here: String
        // TODO: Write out the "parameterstring" property here: String
        // TODO: Write out the "popup" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "secureaccess" property here: String
        // TODO: Write out the "sequence" property here: Integer
        // TODO: Write out the "xdatElementSecurityListingActionId" property here: Integer
    }
}

