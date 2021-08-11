package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementAccessSecureIp;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementAccessSecureIpSerializer<T extends XdatElementAccessSecureIp> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -7175502636145031482L;

    @SuppressWarnings("unchecked")
    public XdatElementAccessSecureIpSerializer() {
        this((Class<T>) XdatElementAccessSecureIp.class);
    }

    protected XdatElementAccessSecureIpSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "secureIp" property here: String
        // TODO: Write out the "xdatElementAccessSecureIpId" property here: Integer
    }
}

