package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectparticipant;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatProjectparticipantSerializer<T extends XnatProjectparticipant> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8786707437345493974L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatProjectparticipantSerializer() {
        this((Class<T>) XnatProjectparticipant.class);
    }

    protected XnatProjectparticipantSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "group", instance.getGroup());
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonBlankField(generator, "project", instance.getProject());
        writeNonBlankField(generator, "subjectId", instance.getSubjectId());
        writeNonNullNumber(generator, "xnatProjectparticipantId", instance.getXnatProjectparticipantId());
    }
}

