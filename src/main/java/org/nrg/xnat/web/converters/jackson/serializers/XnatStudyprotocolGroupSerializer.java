package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolGroup;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatStudyprotocolGroupSerializer<T extends XnatStudyprotocolGroup> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2340862010304839247L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolGroupSerializer() {
        this((Class<T>) XnatStudyprotocolGroup.class);
    }

    protected XnatStudyprotocolGroupSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatStudyprotocolGroupId", instance.getXnatStudyprotocolGroupId());
    }
}

