package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntryMetafield;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CatEntryMetafieldSerializer<T extends CatEntryMetafield> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6121253804234729800L;

    @SuppressWarnings("unchecked")
    public CatEntryMetafieldSerializer() {
        this((Class<T>) CatEntryMetafield.class);
    }

    protected CatEntryMetafieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "catEntryMetafieldId" property here: Integer
        // TODO: Write out the "metafield" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
    }
}

