package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheck;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ValProtocoldataScanCheckDeserializer<T extends ValProtocoldataScanCheck> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -9042143294157679642L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataScanCheckDeserializer() {
        this((Class<T>) ValProtocoldataScanCheck.class);
    }

    protected ValProtocoldataScanCheckDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "additionalval":
                // TODO: Handle the "additionalval" property here: org.nrg.xdat.model.ValAdditionalvalI
                break;
            case "comments_comment":
                // TODO: Handle the "comments_comment" property here: java.util.List
                break;
            case "conditions_condition":
                // TODO: Handle the "conditions_condition" property here: java.util.List
                break;
            case "scanId":
                instance.setScanId(parser.getText());
                break;
            case "status":
                instance.setStatus(parser.getText());
                break;
            case "valProtocoldataScanCheckId":
                instance.setValProtocoldataScanCheckId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

