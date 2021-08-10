package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningassessment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ScrScreeningassessmentDeserializer<T extends ScrScreeningassessment> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -649789051397357648L;

    @SuppressWarnings("unchecked")
    public ScrScreeningassessmentDeserializer() {
        this((Class<T>) ScrScreeningassessment.class);
    }

    public ScrScreeningassessmentDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comments":
                // TODO: Handle the "comments" property here: String
                break;
            case "header":
                // TODO: Handle the "header" property here: String
                break;
            case "imageassessordata":
                // TODO: Handle the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
                break;
            case "pass":
                // TODO: Handle the "pass" property here: String
                break;
            case "precedence":
                // TODO: Handle the "precedence" property here: int
                break;
            case "rater":
                // TODO: Handle the "rater" property here: String
                break;
            case "scans_scan":
                // TODO: Handle the "scans_scan" property here: java.util.List
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

