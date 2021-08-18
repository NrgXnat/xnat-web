package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolCondition;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatStudyprotocolConditionSerializer<T extends XnatStudyprotocolCondition> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1538727363268083086L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolConditionSerializer() {
        this((Class<T>) XnatStudyprotocolCondition.class);
    }

    protected XnatStudyprotocolConditionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatStudyprotocolConditionId", instance.getXnatStudyprotocolConditionId());
    }
}

