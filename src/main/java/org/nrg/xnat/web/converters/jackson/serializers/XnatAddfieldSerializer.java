package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAddfield;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAddfieldSerializer<T extends XnatAddfield> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 9044663839144604311L;

    @SuppressWarnings("unchecked")
    public XnatAddfieldSerializer() {
        this((Class<T>) XnatAddfield.class);
    }

    protected XnatAddfieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "addfield" property here: Object
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatAddfieldId" property here: Integer
    }
}

