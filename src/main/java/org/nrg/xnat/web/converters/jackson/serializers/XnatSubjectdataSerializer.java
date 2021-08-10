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
    protected void serializeImpl(final XnatSubjectdata subject, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
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
//        final List<XnatSubjectassessordataI> experiments = subject.getExperiments_experiment();
//        if (experiments != null && !experiments.isEmpty()) {
//        	experiments.forEach(expe -> {
//    			try {
//    				generator.writeStartObject();
//    				generator.writeObjectField("experiments", expe);
//    				generator.writeEndObject();
//    			} catch (IOException e) {
//    				e.printStackTrace();
//    			} catch (Exception e) {
//    				e.printStackTrace();
//    			}
//    		});
//        }

        //writeNonNullField(generator, "experiments", subject.getExperiments_experiment());

//        final List<XnatSubjectassessordataI> experiments = subject.getExperiments_experiment();
//        if (experiments != null && !experiments.isEmpty()) {
//            generator.writeStartArray();
//            for (final XnatSubjectassessordataI experiment : experiments) {
//                final JsonSerializer<Object> serializer = provider.findValueSerializer(experiment.getClass());
//                if (serializer != null) {
//                    serializer.serialize(experiment, generator, provider);
//                } else {
//                    log.warn("I tried to serialize an experiment of type {} but couldn't find a valid serializer", experiment.getClass());
//                }
//            }
//            generator.writeEndArray();
//        }

//        final List<XnatSubjectdataFieldI> fields = subject.getFields_field();
//        if (!fields.isEmpty()) {
//            generator.writeStartArray();
//            for (final XnatSubjectdataFieldI field : fields) {
//                writeNonBlankField(generator, field.getName(), field.getField());
//            }
//            generator.writeEndArray();
//        }

    }
}
