package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatActionType;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatActionTypeSerializer<T extends XdatActionType> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -4473441261832613382L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatActionTypeSerializer() {
        this((Class<T>) XdatActionType.class);
    }

    protected XdatActionTypeSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "actionName", instance.getActionName());
        writeNonBlankField(generator, "displayName", instance.getDisplayName());
        writeNonNullNumber(generator, "sequence", instance.getSequence());
    }
}

