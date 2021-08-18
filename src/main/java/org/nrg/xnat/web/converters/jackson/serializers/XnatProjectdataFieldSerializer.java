package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdataField;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatProjectdataFieldSerializer<T extends XnatProjectdataField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -847499992686392918L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatProjectdataFieldSerializer() {
        this((Class<T>) XnatProjectdataField.class);
    }

    protected XnatProjectdataFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "field", instance.getField());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatProjectdataFieldId", instance.getXnatProjectdataFieldId());
    }
}

