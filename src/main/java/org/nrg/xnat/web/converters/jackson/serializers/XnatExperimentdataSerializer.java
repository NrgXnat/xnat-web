package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;
import java.util.List;

import org.nrg.xdat.model.XnatExperimentdataFieldI;
import org.nrg.xdat.model.XnatExperimentdataI;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.model.XnatSubjectdataFieldI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xft.security.UserI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XnatExperimentdataSerializer extends AbstractBaseElementSerializer<XnatExperimentdata> {

	public XnatExperimentdataSerializer() {
		super(XnatExperimentdata.class);
	}

	@Override
	protected void serializeImpl(XnatExperimentdata experiment, JsonGenerator generator, SerializerProvider provider)
			throws IOException {
		  System.out.println("##################XnatExperimentdataSerializer#######################");
	       System.out.println("##################XnatExperimentdataSerializer experiment#######################" + experiment);
		writeNonBlankField(generator, "id", experiment.getId());
		writeNonBlankField(generator, "label", experiment.getLabel());
		writeNonBlankField(generator, "project", experiment.getProject());
		writeNonBlankField(generator, "note", experiment.getNote());
		writeNonBlankField(generator, "protocol", experiment.getProtocol());
		writeNonBlankField(generator, "original", experiment.getOriginal());
		writeNonNullField(generator, "date", experiment.getDate());
		writeNonNullNumber(generator, "delay", experiment.getDelay());
		writeNonNullNumber(generator, "version", experiment.getVersion());
		writeNonNullDate(generator, "created", experiment.getInsertDate());
		writeNonBlankField(generator, "xsiType", experiment.getXSIType());
		writeNonBlankField(generator, "description", experiment.getDescription());

		final UserI insertUser = experiment.getInsertUser();
		if (insertUser != null) {
			writeNonBlankField(generator, "createdBy", insertUser.getUsername());
		}

//		final List<XnatExperimentdataFieldI> fields = experiment.getFields_field();
//		if (!fields.isEmpty()) {
//			generator.writeStartArray();
//			for (final XnatExperimentdataFieldI field : fields) {
//				writeNonBlankField(generator, field.getName(), field.getField());
//			}
//			generator.writeEndArray();
//		}
		 System.out.println("##################XnatExperimentdataSerializer generator#######################" + generator);
	}

}
