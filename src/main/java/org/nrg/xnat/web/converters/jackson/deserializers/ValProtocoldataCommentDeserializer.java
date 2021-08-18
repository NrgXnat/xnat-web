package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataComment;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ValProtocoldataCommentDeserializer<T extends ValProtocoldataComment> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7657870092991842756L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataCommentDeserializer() {
        this((Class<T>) ValProtocoldataComment.class);
    }

    protected ValProtocoldataCommentDeserializer(final Class<T> clazz) {
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
            case "valProtocoldataCommentId":
                instance.setValProtocoldataCommentId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

