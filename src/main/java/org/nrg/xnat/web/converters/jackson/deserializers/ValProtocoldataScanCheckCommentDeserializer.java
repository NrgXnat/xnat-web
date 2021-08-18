package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheckComment;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ValProtocoldataScanCheckCommentDeserializer<T extends ValProtocoldataScanCheckComment> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -828329985802111599L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataScanCheckCommentDeserializer() {
        this((Class<T>) ValProtocoldataScanCheckComment.class);
    }

    protected ValProtocoldataScanCheckCommentDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comment":
                instance.setComment(parser.getText());
                break;
            case "datetime":
                // TODO: Handle the "datetime" property here: Object
                break;
            case "username":
                instance.setUsername(parser.getText());
                break;
            case "valProtocoldataScanCheckCommentId":
                instance.setValProtocoldataScanCheckCommentId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

