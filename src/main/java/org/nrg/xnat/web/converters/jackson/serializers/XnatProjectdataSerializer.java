package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatProjectdataSerializer<T extends XnatProjectdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5168583339226819279L;

    @SuppressWarnings("unchecked")
    public XnatProjectdataSerializer() {
        this((Class<T>) XnatProjectdata.class);
    }

    protected XnatProjectdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatProjectdata project, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", project.getId());
        writeNonBlankField(generator, "description", project.getDescription());
        writeNonBlankField(generator, "name", project.getName());
        writeNonBlankField(generator, "secondaryId", project.getSecondaryId());
        writeNonBlankField(generator, "keywords", project.getKeywords());
        writeNonNullBoolean(generator, "active", project.getActive());
        writeNonNullField(generator, "pi", project.getPi());
        writeNonNullField(generator, "investigators", project.getInvestigators_investigator());
        //writeNonBlankField(generator, "firstName", xnatInvestigatordata.getFirstname());
        //writeNonBlankField(generator, "lastName", xnatInvestigatordata.getLastname());
        //writeNonBlankField(generator, "firstName", project.getPi().getFirstname());
        //writeNonBlankField(generator, "firstName", project.getPi().getLastname());


//        final List<XnatProjectassessordataI> experiments = project.getExperiments_experiment();
//        if (experiments != null && !experiments.isEmpty()) {
//            generator.writeStartArray();
//            for (final XnatProjectassessordataI experiment : experiments) {
//                final JsonSerializer<Object> serializer = provider.findValueSerializer(experiment.getClass());
//                if (serializer != null) {
//                    serializer.serialize(experiment, generator, provider);
//                } else {
//                    log.warn("I tried to serialize an experiment of type {} but couldn't find a valid serializer", experiment.getClass());
//                }
//            }
//            generator.writeEndArray();
//        }
//
//        final List<XnatProjectdataI> fields = project.getFields_field();
//        if (!fields.isEmpty()) {
//            generator.writeStartArray();
//            for (final XnatSubjectdataFieldI field : fields) {
//                writeNonBlankField(generator, field.getName(), field.getField());
//            }
//            generator.writeEndArray();
//        }

    }
}
