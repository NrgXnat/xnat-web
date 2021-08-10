package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningassessment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ScrScreeningassessmentSerializer<T extends ScrScreeningassessment> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4858531819705215841L;

    @SuppressWarnings("unchecked")
    public ScrScreeningassessmentSerializer() {
        this((Class<T>) ScrScreeningassessment.class);
    }

    protected ScrScreeningassessmentSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "comments" property here: String
        // TODO: Write out the "header" property here: String
        // TODO: Write out the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
        // TODO: Write out the "pass" property here: String
        // TODO: Write out the "precedence" property here: int
        // TODO: Write out the "rater" property here: String
        // TODO: Write out the "scans_scan" property here: java.util.List
        // TODO: Write out the "schemaElementName" property here: String
    }
}

