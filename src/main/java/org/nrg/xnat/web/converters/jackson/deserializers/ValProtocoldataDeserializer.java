package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ValProtocoldataDeserializer<T extends ValProtocoldata> extends XnatImageassessordataDeserializer<T> {
    private static final long serialVersionUID = -3254441620342147965L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataDeserializer() {
        this((Class<T>) ValProtocoldata.class);
    }

    protected ValProtocoldataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "check_additionalval":
                // TODO: Handle the "check_additionalval" property here: org.nrg.xdat.om.ValAdditionalval
                break;
            case "check_comments_comment":
                // TODO: Handle the "check_comments_comment" property here: java.util.List
                break;
            case "check_conditions_condition":
                // TODO: Handle the "check_conditions_condition" property here: java.util.List
                break;
            case "check_status":
                instance.setCheck_status(parser.getText());
                break;
            case "scans_scanCheck":
                // TODO: Handle the "scans_scanCheck" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

