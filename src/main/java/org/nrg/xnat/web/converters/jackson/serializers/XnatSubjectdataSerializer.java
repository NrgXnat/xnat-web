package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XnatSubjectdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectdataSerializer<T extends XnatSubjectdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5781170250219703398L;

    @SuppressWarnings("unchecked")
    public XnatSubjectdataSerializer() {
        this((Class<T>) XnatSubjectdata.class);
    }

    protected XnatSubjectdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T subject, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", subject.getId());
        writeNonBlankField(generator, "label", subject.getLabel());
        writeNonBlankField(generator, "project", subject.getProject());
        writeNonBlankField(generator, "group", subject.getGroup());
        writeNonNullNumber(generator, "ses", subject.getSes());
        writeNonBlankField(generator, "initials", subject.getInitials());
        writeNonNullField(generator, "demographics", subject.getDemographics());

        generator.writeArrayFieldStart("experiments");
        for (final XnatSubjectassessordataI experiment : subject.getExperiments_experiment()) {
            generator.writeString(experiment.getId());
        }
        generator.writeEndArray();
    }
}
