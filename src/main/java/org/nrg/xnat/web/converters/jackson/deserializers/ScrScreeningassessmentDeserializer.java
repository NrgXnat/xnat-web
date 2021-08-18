package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningassessment;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ScrScreeningassessmentDeserializer<T extends ScrScreeningassessment> extends XnatImageassessordataDeserializer<T> {
    private static final long serialVersionUID = -1368698616061215608L;

    @SuppressWarnings({"unchecked", "unused"})
    public ScrScreeningassessmentDeserializer() {
        this((Class<T>) ScrScreeningassessment.class);
    }

    protected ScrScreeningassessmentDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comments":
                instance.setComments(parser.getText());
                break;
            case "pass":
                instance.setPass(parser.getText());
                break;
            case "rater":
                instance.setRater(parser.getText());
                break;
            case "scans_scan":
                // TODO: Handle the "scans_scan" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

