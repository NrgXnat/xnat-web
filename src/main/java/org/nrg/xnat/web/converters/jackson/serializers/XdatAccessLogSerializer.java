package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatAccessLog;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatAccessLogSerializer<T extends XdatAccessLog> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3457438996827779118L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatAccessLogSerializer() {
        this((Class<T>) XdatAccessLog.class);
    }

    protected XdatAccessLogSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "accessDate" property here: Object
        writeNonBlankField(generator, "ip", instance.getIp());
        writeNonBlankField(generator, "login", instance.getLogin());
        writeNonBlankField(generator, "method", instance.getMethod());
        writeNonNullNumber(generator, "xdatAccessLogId", instance.getXdatAccessLogId());
    }
}

