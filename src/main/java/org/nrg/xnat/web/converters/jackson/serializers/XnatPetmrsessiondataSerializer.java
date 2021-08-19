package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetmrsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPetmrsessiondataSerializer<T extends XnatPetmrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 8704374709330860057L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetmrsessiondataSerializer() {
        this((Class<T>) XnatPetmrsessiondata.class);
    }

    protected XnatPetmrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "bloodGlucose", instance.getBloodGlucose());
        // TODO: Write out the "bloodGlucoseTime" property here: Object
        generator.writeStartObject();
        generator.writeObjectField("bloodGlucoseTime", instance.getBloodGlucoseTime());
        generator.writeEndObject();
        writeNonBlankField(generator, "bloodGlucoseUnits", instance.getBloodGlucoseUnits());
        writeNonBlankField(generator, "coil", instance.getCoil());
        writeNonBlankField(generator, "fieldStrength", instance.getFieldstrength());
        writeNonBlankField(generator, "marker", instance.getMarker());
        writeNonBlankField(generator, "patientId", instance.getPatientid());
        writeNonBlankField(generator, "patientName", instance.getPatientname());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
        // TODO: Write out the "startTime" property here: Object
        generator.writeStartObject();
        generator.writeObjectField("startTime", instance.getStartTime());
        generator.writeEndObject();
        // TODO: Write out the "startTimeInjection" property here: Object
        generator.writeStartObject();
        generator.writeObjectField("startTimeInjection", instance.getStartTimeInjection());
        generator.writeEndObject();
        // TODO: Write out the "startTimeScan" property here: Object
        generator.writeStartObject();
        generator.writeObjectField("startTimeScan", instance.getStartTimeScan());
        generator.writeEndObject();
        writeNonBlankField(generator, "studyType", instance.getStudytype());
        writeNonNullNumber(generator, "tracerDose", instance.getTracer_dose());
        writeNonBlankField(generator, "tracerDoseUnits", instance.getTracer_dose_units());
        writeNonNullNumber(generator, "tracerIntermediate", instance.getTracer_intermediate());
        writeNonBlankField(generator, "tracerIntermediateUnits", instance.getTracer_intermediate_units());
        writeNonBlankField(generator, "tracerIsotope", instance.getTracer_isotope());
        writeNonNullNumber(generator, "tracerIsotopeHalfLife", instance.getTracer_isotope_halfLife());
        writeNonBlankField(generator, "tracerName", instance.getTracer_name());
        writeNonNullNumber(generator, "tracerSpecificActivity", instance.getTracer_specificactivity());
        // TODO: Write out the "tracer_starttime" property here: Object
        generator.writeStartObject();
        generator.writeObjectField("tracerStartTime", instance.getTracer_starttime());
        generator.writeEndObject();
        writeNonNullNumber(generator, "tracerTotalMass", instance.getTracer_totalmass());
        writeNonBlankField(generator, "tracerTotalMassUnits", instance.getTracer_totalmass_units());
        writeNonNullNumber(generator, "tracerTransmissions", instance.getTracer_transmissions());
        // TODO: Write out the "tracer_transmissionsStarttime" property here: Object
        generator.writeStartObject();
        generator.writeObjectField("tracerTransmissionsStartTime", instance.getTracer_transmissionsStarttime());
        generator.writeEndObject();
        super.serializeImpl(instance, generator, provider);
    }
}

