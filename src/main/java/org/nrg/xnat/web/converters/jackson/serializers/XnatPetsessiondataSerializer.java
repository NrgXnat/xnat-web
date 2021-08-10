package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetsessiondata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

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
    protected void serializeImpl(final XnatPetsessiondata xnatPetsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatPetsessiondata, generator, provider);
        writeNonBlankField(generator, "id", xnatPetsessiondata.getId());
        writeNonBlankField(generator, "stabilization", xnatPetsessiondata.getStabilization());
        writeNonBlankField(generator, "studyType", xnatPetsessiondata.getStudytype());
        writeNonBlankField(generator, "patientID", xnatPetsessiondata.getPatientid());
        writeNonBlankField(generator, "patientName", xnatPetsessiondata.getPatientname());
        writeNonBlankField(generator, "tracer", xnatPetsessiondata.getTracer_name());
        writeNonNullField(generator, "start_time", xnatPetsessiondata.getStartTime());
        writeNonNullField(generator, "start_time_scan", xnatPetsessiondata.getStartTimeScan());
        writeNonNullField(generator, "start_time_injection", xnatPetsessiondata.getStartTimeInjection());
        writeNonNullField(generator, "blood_glucose", xnatPetsessiondata.getBloodGlucose());
        writeNonBlankField(generator, "blood_glucose_units", xnatPetsessiondata.getBloodGlucoseUnits());
        writeNonNullField(generator, "blood_glucose_time", xnatPetsessiondata.getBloodGlucoseTime());
    }

}
