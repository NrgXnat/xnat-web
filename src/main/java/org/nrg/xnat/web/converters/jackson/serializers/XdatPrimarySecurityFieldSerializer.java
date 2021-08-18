package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatPrimarySecurityField;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatPrimarySecurityFieldSerializer<T extends XdatPrimarySecurityField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8219004500132964988L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatPrimarySecurityFieldSerializer() {
        this((Class<T>) XdatPrimarySecurityField.class);
    }

    protected XdatPrimarySecurityFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "primarySecurityField", instance.getPrimarySecurityField());
        writeNonNullNumber(generator, "xdatPrimarySecurityFieldId", instance.getXdatPrimarySecurityFieldId());
    }
}

