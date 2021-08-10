package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetsessiondataSerializer<T extends XnatPetsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -7581439107130807310L;

    @SuppressWarnings("unchecked")
    public XnatPetsessiondataSerializer() {
        this((Class<T>) XnatPetsessiondata.class);
    }

    protected XnatPetsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
        writeNonBlankField(generator, "studyType", instance.getStudytype());
        writeNonBlankField(generator, "patientID", instance.getPatientid());
        writeNonBlankField(generator, "patientName", instance.getPatientname());
        writeNonBlankField(generator, "tracer", instance.getTracer_name());
        writeNonNullField(generator, "start_time", instance.getStartTime());
        writeNonNullField(generator, "start_time_scan", instance.getStartTimeScan());
        writeNonNullField(generator, "start_time_injection", instance.getStartTimeInjection());
        writeNonNullField(generator, "blood_glucose", instance.getBloodGlucose());
        writeNonBlankField(generator, "blood_glucose_units", instance.getBloodGlucoseUnits());
        writeNonNullField(generator, "blood_glucose_time", instance.getBloodGlucoseTime());
    }

}
