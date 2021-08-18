package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurityListingAction;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatElementSecurityListingActionSerializer<T extends XdatElementSecurityListingAction> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1193274751520228776L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementSecurityListingActionSerializer() {
        this((Class<T>) XdatElementSecurityListingAction.class);
    }

    protected XdatElementSecurityListingActionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "displayName", instance.getDisplayName());
        writeNonBlankField(generator, "elementActionName", instance.getElementActionName());
        writeNonBlankField(generator, "image", instance.getImage());
        writeNonBlankField(generator, "parameterstring", instance.getParameterstring());
        writeNonBlankField(generator, "popup", instance.getPopup());
        writeNonBlankField(generator, "secureaccess", instance.getSecureaccess());
        writeNonNullNumber(generator, "sequence", instance.getSequence());
        writeNonNullNumber(generator, "xdatElementSecurityListingActionId", instance.getXdatElementSecurityListingActionId());
    }
}

