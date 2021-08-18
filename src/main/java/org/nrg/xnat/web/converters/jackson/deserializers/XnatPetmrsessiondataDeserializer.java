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
            case "fieldstrength":
                instance.setFieldstrength(parser.getText());
                break;
            case "marker":
                instance.setMarker(parser.getText());
                break;
            case "patientid":
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
            case "studytype":
                instance.setStudytype(parser.getText());
                break;
            case "tracer_dose":
                instance.setTracer_dose(parser.getDoubleValue());
                break;
            case "tracer_dose_units":
                instance.setTracer_dose_units(parser.getText());
                break;
            case "tracer_intermediate":
                instance.setTracer_intermediate(parser.getDoubleValue());
                break;
            case "tracer_intermediate_units":
                instance.setTracer_intermediate_units(parser.getText());
                break;
            case "tracer_isotope":
                instance.setTracer_isotope(parser.getText());
                break;
            case "tracer_isotope_halfLife":
                instance.setTracer_isotope_halfLife(parser.getDoubleValue());
                break;
            case "tracer_name":
                instance.setTracer_name(parser.getText());
                break;
            case "tracer_specificactivity":
                instance.setTracer_specificactivity(parser.getDoubleValue());
                break;
            case "tracer_starttime":
                // TODO: Handle the "tracer_starttime" property here: Object
                break;
            case "tracer_totalmass":
                instance.setTracer_totalmass(parser.getDoubleValue());
                break;
            case "tracer_totalmass_units":
                instance.setTracer_totalmass_units(parser.getText());
                break;
            case "tracer_transmissions":
                instance.setTracer_transmissions(parser.getIntValue());
                break;
            case "tracer_transmissionsStarttime":
                // TODO: Handle the "tracer_transmissionsStarttime" property here: Object
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

