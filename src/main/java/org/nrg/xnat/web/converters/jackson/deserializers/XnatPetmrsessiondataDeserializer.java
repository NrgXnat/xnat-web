package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetmrsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPetmrsessiondataDeserializer<T extends XnatPetmrsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -4654262586792043038L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetmrsessiondataDeserializer() {
        this((Class<T>) XnatPetmrsessiondata.class);
    }

    protected XnatPetmrsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "bloodGlucose":
                instance.setBloodGlucose(parser.getDoubleValue());
                break;
            case "bloodGlucoseTime":
                // TODO: Handle the "bloodGlucoseTime" property here: Object
                break;
            case "bloodGlucoseUnits":
                instance.setBloodGlucoseUnits(parser.getText());
                break;
            case "coil":
                instance.setCoil(parser.getText());
                break;
            case "fieldStrength":
                instance.setFieldstrength(parser.getText());
                break;
            case "marker":
                instance.setMarker(parser.getText());
                break;
            case "patientId":
                instance.setPatientid(parser.getText());
                break;
            case "patientname":
                instance.setPatientname(parser.getText());
                break;
            case "stabilization":
                instance.setStabilization(parser.getText());
                break;
            case "startTime":
                // TODO: Handle the "startTime" property here: Object
                break;
            case "startTimeInjection":
                // TODO: Handle the "startTimeInjection" property here: Object
                break;
            case "startTimeScan":
                // TODO: Handle the "startTimeScan" property here: Object
                break;
            case "studyType":
                instance.setStudytype(parser.getText());
                break;
            case "tracerDose":
                instance.setTracer_dose(parser.getDoubleValue());
                break;
            case "tracerDoseUnits":
                instance.setTracer_dose_units(parser.getText());
                break;
            case "tracerIntermediate":
                instance.setTracer_intermediate(parser.getDoubleValue());
                break;
            case "tracerIntermediateUnits":
                instance.setTracer_intermediate_units(parser.getText());
                break;
            case "tracerIsotope":
                instance.setTracer_isotope(parser.getText());
                break;
            case "tracerIsotopeHalfLife":
                instance.setTracer_isotope_halfLife(parser.getDoubleValue());
                break;
            case "tracerName":
                instance.setTracer_name(parser.getText());
                break;
            case "tracerSpecificActivity":
                instance.setTracer_specificactivity(parser.getDoubleValue());
                break;
            case "tracerStartTime":
                // TODO: Handle the "tracer_starttime" property here: Object
                break;
            case "tracerTotalMass":
                instance.setTracer_totalmass(parser.getDoubleValue());
                break;
            case "tracerTotalMassUnits":
                instance.setTracer_totalmass_units(parser.getText());
                break;
            case "tracerTransmissions":
                instance.setTracer_transmissions(parser.getIntValue());
                break;
            case "tracerTransmissionsStartTime":
                // TODO: Handle the "tracer_transmissionsStarttime" property here: Object
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

