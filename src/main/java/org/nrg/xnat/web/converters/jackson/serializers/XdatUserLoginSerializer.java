package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUserLogin;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatUserLoginSerializer<T extends XdatUserLogin> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1298154013617479646L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatUserLoginSerializer() {
        this((Class<T>) XdatUserLogin.class);
    }

    protected XdatUserLoginSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "ipAddress", instance.getIpAddress());
        // TODO: Write out the "loginDate" property here: Object
        writeNonBlankField(generator, "nodeId", instance.getNodeId());
        // TODO: Write out the "userProperty" property here: org.nrg.xdat.om.XdatUserI
        writeNonNullNumber(generator, "xdatUserLoginId", instance.getXdatUserLoginId());
    }
}

