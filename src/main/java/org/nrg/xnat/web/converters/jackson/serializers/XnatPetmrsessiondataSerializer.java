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
        writeNonBlankField(generator, "bloodGlucoseUnits", instance.getBloodGlucoseUnits());
        writeNonBlankField(generator, "coil", instance.getCoil());
        writeNonBlankField(generator, "fieldstrength", instance.getFieldstrength());
        writeNonBlankField(generator, "marker", instance.getMarker());
        writeNonBlankField(generator, "patientid", instance.getPatientid());
        writeNonBlankField(generator, "patientname", instance.getPatientname());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
        // TODO: Write out the "startTime" property here: Object
        // TODO: Write out the "startTimeInjection" property here: Object
        // TODO: Write out the "startTimeScan" property here: Object
        writeNonBlankField(generator, "studytype", instance.getStudytype());
        writeNonNullNumber(generator, "tracer_dose", instance.getTracer_dose());
        writeNonBlankField(generator, "tracer_dose_units", instance.getTracer_dose_units());
        writeNonNullNumber(generator, "tracer_intermediate", instance.getTracer_intermediate());
        writeNonBlankField(generator, "tracer_intermediate_units", instance.getTracer_intermediate_units());
        writeNonBlankField(generator, "tracer_isotope", instance.getTracer_isotope());
        writeNonNullNumber(generator, "tracer_isotope_halfLife", instance.getTracer_isotope_halfLife());
        writeNonBlankField(generator, "tracer_name", instance.getTracer_name());
        writeNonNullNumber(generator, "tracer_specificactivity", instance.getTracer_specificactivity());
        // TODO: Write out the "tracer_starttime" property here: Object
        writeNonNullNumber(generator, "tracer_totalmass", instance.getTracer_totalmass());
        writeNonBlankField(generator, "tracer_totalmass_units", instance.getTracer_totalmass_units());
        writeNonNullNumber(generator, "tracer_transmissions", instance.getTracer_transmissions());
        // TODO: Write out the "tracer_transmissionsStarttime" property here: Object
        super.serializeImpl(instance, generator, provider);
    }
}

