package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningassessment;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ScrScreeningassessmentSerializer<T extends ScrScreeningassessment> extends XnatImageassessordataSerializer<T> {
    private static final long serialVersionUID = 2136538157053258553L;

    @SuppressWarnings({"unchecked", "unused"})
    public ScrScreeningassessmentSerializer() {
        this((Class<T>) ScrScreeningassessment.class);
    }

    protected ScrScreeningassessmentSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "comments", instance.getComments());
        writeNonBlankField(generator, "pass", instance.getPass());
        writeNonBlankField(generator, "rater", instance.getRater());
        // TODO: Write out the "scans_scan" property here: java.util.List
        super.serializeImpl(instance, generator, provider);
    }
}

