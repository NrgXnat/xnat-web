package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDatatypeprotocol;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatDatatypeprotocolDeserializer<T extends XnatDatatypeprotocol> extends XnatAbstractprotocolDeserializer<T> {
    private static final long serialVersionUID = 5144822350106912358L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDatatypeprotocolDeserializer() {
        this((Class<T>) XnatDatatypeprotocol.class);
    }

    protected XnatDatatypeprotocolDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "definitions_definition":
                // TODO: Handle the "definitions_definition" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

