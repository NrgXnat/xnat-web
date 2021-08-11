package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataComment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValProtocoldataCommentDeserializer<T extends ValProtocoldataComment> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4879847806607735992L;

    @SuppressWarnings("unchecked")
    public ValProtocoldataCommentDeserializer() {
        this((Class<T>) ValProtocoldataComment.class);
    }

    public ValProtocoldataCommentDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comment":
                // TODO: Handle the "comment" property here: String
                break;
            case "datetime":
                // TODO: Handle the "datetime" property here: Object
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "username":
                // TODO: Handle the "username" property here: String
                break;
            case "valProtocoldataCommentId":
                // TODO: Handle the "valProtocoldataCommentId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

