package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentNotify;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class WrkXnatexecutionenvironmentNotifySerializer<T extends WrkXnatexecutionenvironmentNotify> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2990103081162874062L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkXnatexecutionenvironmentNotifySerializer() {
        this((Class<T>) WrkXnatexecutionenvironmentNotify.class);
    }

    protected WrkXnatexecutionenvironmentNotifySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "notify", instance.getNotify());
        writeNonNullNumber(generator, "wrkXnatexecutionenvironmentNotifyId", instance.getWrkXnatexecutionenvironmentNotifyId());
    }
}

