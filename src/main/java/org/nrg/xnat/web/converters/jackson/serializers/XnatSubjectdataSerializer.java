package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.model.XnatSubjectdataFieldI;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;

import java.io.IOException;
import java.util.List;

@Slf4j
public class XnatSubjectdataSerializer extends AbstractBaseElementSerializer<XnatSubjectdata> {
    public XnatSubjectdataSerializer() {
        super(XnatSubjectdata.class);
    }

    @Override
    protected void serializeImpl(final XnatSubjectdata subject, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
       System.out.println("##################XnatSubjectdataSerializer#######################");
       System.out.println("##################subject#######################" + subject);
    	writeNonBlankField(generator, "id", subject.getId());
        writeNonBlankField(generator, "label", subject.getLabel());
        writeNonBlankField(generator, "project", subject.getProject());
        writeNonBlankField(generator, "group", subject.getGroup());
        writeNonBlankField(generator, "dob", subject.getDOBDisplay());
        writeNonBlankField(generator, "educationDesc", subject.getEducationDesc());
        writeNonNullNumber(generator, "education", subject.getEducation());
        writeNonNullNumber(generator, "age", subject.getAge());
        writeNonNullNumber(generator, "ses", subject.getSes());
        writeNonBlankField(generator, "gender", subject.getGender());
        writeNonBlankField(generator, "handedness", subject.getHandedness());
        writeNonBlankField(generator, "ethnicity", subject.getEthnicity());
        writeNonBlankField(generator, "race", subject.getRace());
        writeNonBlankField(generator, "initials", subject.getInitials());
        writeNonNullDate(generator, "created", subject.getInsertDate());

        final UserI insertUser = subject.getInsertUser();
        if (insertUser != null) {
            writeNonBlankField(generator, "createdBy", insertUser.getUsername());
        }

        final List<XnatSubjectassessordataI> experiments = subject.getExperiments_experiment();
        if (experiments != null && !experiments.isEmpty()) {
            generator.writeStartArray();
            for (final XnatSubjectassessordataI experiment : experiments) {
                final JsonSerializer<Object> serializer = provider.findValueSerializer(experiment.getClass());
                if (serializer != null) {
                    serializer.serialize(experiment, generator, provider);
                } else {
                    log.warn("I tried to serialize an experiment of type {} but couldn't find a valid serializer", experiment.getClass());
                }
            }
            generator.writeEndArray();
        }

        final List<XnatSubjectdataFieldI> fields = subject.getFields_field();
        if (!fields.isEmpty()) {
            generator.writeStartArray();
            for (final XnatSubjectdataFieldI field : fields) {
                writeNonBlankField(generator, field.getName(), field.getField());
            }
            generator.writeEndArray();
        }
        
        System.out.println("##################generator#######################" + generator);
    }
}
