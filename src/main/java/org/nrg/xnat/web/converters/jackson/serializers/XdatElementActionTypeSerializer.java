package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementActionType;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatElementActionTypeSerializer<T extends XdatElementActionType> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6799689820540613247L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementActionTypeSerializer() {
        this((Class<T>) XdatElementActionType.class);
    }

    protected XdatElementActionTypeSerializer(final Class<T> clazz) {
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
        writeNonBlankField(generator, "securefeature", instance.getSecurefeature());
        writeNonNullNumber(generator, "sequence", instance.getSequence());
        writeNonNullNumber(generator, "xdatElementActionTypeId", instance.getXdatElementActionTypeId());
    }
}

