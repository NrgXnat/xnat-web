package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprotocol;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractprotocolSerializer<T extends XnatAbstractprotocol> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6722564158384602535L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAbstractprotocolSerializer() {
        this((Class<T>) XnatAbstractprotocol.class);
    }

    protected XnatAbstractprotocolSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "dataType", instance.getDataType());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatAbstractprotocolId", instance.getXnatAbstractprotocolId());
    }
}

