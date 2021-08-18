package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementAccessSecureIp;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatElementAccessSecureIpSerializer<T extends XdatElementAccessSecureIp> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -7175502636145031482L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementAccessSecureIpSerializer() {
        this((Class<T>) XdatElementAccessSecureIp.class);
    }

    protected XdatElementAccessSecureIpSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "secureIp", instance.getSecureIp());
        writeNonNullNumber(generator, "xdatElementAccessSecureIpId", instance.getXdatElementAccessSecureIpId());
    }
}

