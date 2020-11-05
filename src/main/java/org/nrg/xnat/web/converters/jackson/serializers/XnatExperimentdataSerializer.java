package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdata;

import java.io.IOException;

@Slf4j
public class XnatExperimentdataSerializer extends AbstractBaseElementSerializer<XnatExperimentdata> {
    protected XnatExperimentdataSerializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    public void serializeImpl(final XnatExperimentdata experiment, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", experiment.getId());
        writeNonBlankField(generator, "label", experiment.getLabel());
        writeNonBlankField(generator, "project", experiment.getProject());
        writeNonBlankField(generator, "note", experiment.getNote());
        writeNonBlankField(generator, "protocol", experiment.getProtocol());
        writeNonBlankField(generator, "original", experiment.getOriginal());
        writeNonNullField(generator, "date", experiment.getDate());
        writeNonNullNumber(generator, "delay", experiment.getDelay());
        writeNonNullNumber(generator, "version", experiment.getVersion());
        writeNonBlankField(generator, "description", experiment.getDescription());

//		final List<XnatExperimentdataFieldI> fields = experiment.getFields_field();
//		if (!fields.isEmpty()) {
//			generator.writeStartArray();
//			for (final XnatExperimentdataFieldI field : fields) {
//				writeNonBlankField(generator, field.getName(), field.getField());
//			}
//			generator.writeEndArray();
//		}
    }
}
