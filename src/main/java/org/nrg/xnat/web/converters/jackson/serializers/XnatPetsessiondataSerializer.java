package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPetsessiondataSerializer<T extends XnatPetsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -3984047040193415387L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetsessiondataSerializer() {
        this((Class<T>) XnatPetsessiondata.class);
    }

    protected XnatPetsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: See XnatPetsessiondataDeserializer for info on serializing/deserializing these hierarchical properties.
        writeNonNullNumber(generator, "bloodGlucose", instance.getBloodGlucose());
        // TODO: Write out the "bloodGlucoseTime" property here: Object
        writeNonBlankField(generator, "bloodGlucoseUnits", instance.getBloodGlucoseUnits());
        writeNonBlankField(generator, "patientId", instance.getPatientid());
        writeNonBlankField(generator, "patientName", instance.getPatientname());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
        // TODO: Write out the "startTime" property here: Object
        // TODO: Write out the "startTimeInjection" property here: Object
        // TODO: Write out the "startTimeScan" property here: Object
        writeNonBlankField(generator, "studyType", instance.getStudytype());
        writeNonNullNumber(generator, "tracerDose", instance.getTracer_dose());
        writeNonBlankField(generator, "tracerDoseUnits", instance.getTracer_dose_units());
        writeNonNullNumber(generator, "tracerIntermediate", instance.getTracer_intermediate());
        writeNonBlankField(generator, "tracerIntermediateUnits", instance.getTracer_intermediate_units());
        writeNonBlankField(generator, "tracerIsotope", instance.getTracer_isotope());
        writeNonNullNumber(generator, "tracerIsotopeHalfLife", instance.getTracer_isotope_halfLife());
        writeNonBlankField(generator, "tracerName", instance.getTracer_name());
        writeNonNullNumber(generator, "tracerSpecificActivity", instance.getTracer_specificactivity());
        // TODO: Write out the "tracerStartTime" property here: Object
        writeNonNullNumber(generator, "tracerTotalMass", instance.getTracer_totalmass());
        writeNonBlankField(generator, "tracerTotalMassUnits", instance.getTracer_totalmass_units());
        writeNonNullNumber(generator, "tracerTransmissions", instance.getTracer_transmissions());
        // TODO: Write out the "tracerTransmissionsStarttime" property here: Object
        super.serializeImpl(instance, generator, provider);
    }
}
