package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheckComment;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ValProtocoldataScanCheckCommentSerializer<T extends ValProtocoldataScanCheckComment> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4869060408034511203L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataScanCheckCommentSerializer() {
        this((Class<T>) ValProtocoldataScanCheckComment.class);
    }

    protected ValProtocoldataScanCheckCommentSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "comment", instance.getComment());
        // TODO: Write out the "datetime" property here: Object
        writeNonBlankField(generator, "username", instance.getUsername());
        writeNonNullNumber(generator, "valProtocoldataScanCheckCommentId", instance.getValProtocoldataScanCheckCommentId());
    }
}

