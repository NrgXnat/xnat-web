package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementAccess;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatElementAccessSerializer<T extends XdatElementAccess> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1598959261225803509L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementAccessSerializer() {
        this((Class<T>) XdatElementAccess.class);
    }

    protected XdatElementAccessSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "elementName", instance.getElementName());
        // TODO: Write out the "permissions_allowSet" property here: java.util.ArrayList
        writeNonBlankField(generator, "secondaryPassword", instance.getSecondaryPassword());
        writeNonNullBoolean(generator, "secondaryPassword_encrypt", instance.getSecondaryPassword_encrypt());
        // TODO: Write out the "secureIp" property here: java.util.ArrayList
        writeNonNullNumber(generator, "xdatElementAccessId", instance.getXdatElementAccessId());
    }
}