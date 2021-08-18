package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectdataField;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSubjectdataFieldSerializer<T extends XnatSubjectdataField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8848543886827005457L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectdataFieldSerializer() {
        this((Class<T>) XnatSubjectdataField.class);
    }

    protected XnatSubjectdataFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "field", instance.getField());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatSubjectdataFieldId", instance.getXnatSubjectdataFieldId());
    }
}

