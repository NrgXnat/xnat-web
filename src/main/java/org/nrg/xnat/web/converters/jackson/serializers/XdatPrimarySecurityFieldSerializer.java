package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatPrimarySecurityField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatPrimarySecurityFieldSerializer<T extends XdatPrimarySecurityField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8219004500132964988L;

    @SuppressWarnings("unchecked")
    public XdatPrimarySecurityFieldSerializer() {
        this((Class<T>) XdatPrimarySecurityField.class);
    }

    protected XdatPrimarySecurityFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "primarySecurityField" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xdatPrimarySecurityFieldId" property here: Integer
    }
}

