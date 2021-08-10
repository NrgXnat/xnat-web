package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatAccessLog;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatAccessLogSerializer<T extends XdatAccessLog> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3457438996827779118L;

    @SuppressWarnings("unchecked")
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
        // TODO: Write out the "login" property here: String
        // TODO: Write out the "method" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xdatAccessLogId" property here: Integer
    }
}

