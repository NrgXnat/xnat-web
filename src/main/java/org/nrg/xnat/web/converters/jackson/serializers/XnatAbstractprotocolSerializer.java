package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprotocol;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractprotocolSerializer<T extends XnatAbstractprotocol> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6912627144885118417L;

    @SuppressWarnings("unchecked")
    public XnatAbstractprotocolSerializer() {
        this((Class<T>) XnatAbstractprotocol.class);
    }

    protected XnatAbstractprotocolSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T protocol, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullField(generator, "xnatAbstractProtocolId", protocol.getXnatAbstractprotocolId());
        writeNonBlankField(generator, "name", protocol.getName());
        writeNonBlankField(generator, "id", protocol.getId());
        writeNonBlankField(generator, "dataType", protocol.getDataType());
        writeNonBlankField(generator, "description", protocol.getDescription());
        writeNonBlankField(generator, "xsiType", protocol.getXSIType());
    }
}
