package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataComment;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ValProtocoldataCommentSerializer<T extends ValProtocoldataComment> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5125159012067792713L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataCommentSerializer() {
        this((Class<T>) ValProtocoldataComment.class);
    }

    protected ValProtocoldataCommentSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "comment", instance.getComment());
        // TODO: Write out the "datetime" property here: Object
        writeNonBlankField(generator, "username", instance.getUsername());
        writeNonNullNumber(generator, "valProtocoldataCommentId", instance.getValProtocoldataCommentId());
    }
}

