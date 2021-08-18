package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagescandataShare;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatImagescandataShareSerializer<T extends XnatImagescandataShare> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -799840610509432988L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatImagescandataShareSerializer() {
        this((Class<T>) XnatImagescandataShare.class);
    }

    protected XnatImagescandataShareSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonBlankField(generator, "project", instance.getProject());
        writeNonBlankField(generator, "share", instance.getShare());
        writeNonNullNumber(generator, "xnatImagescandataShareId", instance.getXnatImagescandataShareId());
    }
}

