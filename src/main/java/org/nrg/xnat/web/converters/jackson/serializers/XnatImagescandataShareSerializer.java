package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagescandataShare;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatImagescandataShareSerializer<T extends XnatImagescandataShare> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -799840610509432988L;

    @SuppressWarnings("unchecked")
    public XnatImagescandataShareSerializer() {
        this((Class<T>) XnatImagescandataShare.class);
    }

    protected XnatImagescandataShareSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "label" property here: String
        // TODO: Write out the "project" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "share" property here: String
        // TODO: Write out the "xnatImagescandataShareId" property here: Integer
    }
}

