package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.model.XnatFielddefinitiongroupI;
import org.nrg.xdat.om.XnatDatatypeprotocol;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDatatypeprotocolSerializer<T extends XnatDatatypeprotocol> extends XnatAbstractprotocolSerializer<T> {
    private static final long serialVersionUID = -6912627144885118417L;

    @SuppressWarnings("unchecked")
    public XnatDatatypeprotocolSerializer() {
        this((Class<T>) XnatDatatypeprotocol.class);
    }

    protected XnatDatatypeprotocolSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T protocol, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(protocol, generator, provider);
        generator.writeArrayFieldStart("definition");
        for (final XnatFielddefinitiongroupI definition : protocol.getDefinitions_definition()) {
            generator.writeString(definition.getId());
        }
        generator.writeEndArray();
    }
}
