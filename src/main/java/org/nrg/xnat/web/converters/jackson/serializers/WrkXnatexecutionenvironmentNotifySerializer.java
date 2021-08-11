package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentNotify;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkXnatexecutionenvironmentNotifySerializer<T extends WrkXnatexecutionenvironmentNotify> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2990103081162874062L;

    @SuppressWarnings("unchecked")
    public WrkXnatexecutionenvironmentNotifySerializer() {
        this((Class<T>) WrkXnatexecutionenvironmentNotify.class);
    }

    protected WrkXnatexecutionenvironmentNotifySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "notify" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "wrkXnatexecutionenvironmentNotifyId" property here: Integer
    }
}

