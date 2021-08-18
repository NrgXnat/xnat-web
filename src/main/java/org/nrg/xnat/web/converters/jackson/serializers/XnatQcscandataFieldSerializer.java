package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcscandataField;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatQcscandataFieldSerializer<T extends XnatQcscandataField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6894377256071011485L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcscandataFieldSerializer() {
        this((Class<T>) XnatQcscandataField.class);
    }

    protected XnatQcscandataFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "field", instance.getField());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatQcscandataFieldId", instance.getXnatQcscandataFieldId());
    }
}

