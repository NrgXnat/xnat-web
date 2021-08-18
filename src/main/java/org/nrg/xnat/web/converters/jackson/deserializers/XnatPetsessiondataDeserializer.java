package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPetsessiondataDeserializer<T extends XnatPetsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -7377326668367557003L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetsessiondataDeserializer() {
        this((Class<T>) XnatPetsessiondata.class);
    }

    protected XnatPetsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        /*
        TODO: Instead of serializing directly, it'd be nice to have the map structure implied by many of the property names.
         In this specific case, we have the following properties:
        
         * start_time
         * start_time_scan
         * start_time_injection
         * blood_glucose
         * blood_glucose_units
         * blood_glucose_time

        This is represented in JSON directly like this:

         {
             "start_time": "foo",
             "start_time_scan": "foo",
             "start_time_injection": "foo",
             "blood_glucose": "foo",
             "blood_glucose_units": "foo",
             "blood_glucose_time": "foo"
         }

        But what's implied by the prefix is that these are all part of an object-like thing. It would be easier to parse and
        make more sense if the JSON looked like this:

         {
             "startTime": {
                 "value": "foo",
                 "scan": "foo",
                 "injection": "foo"
             },
             "bloodGlucose": {
                 "level": "foo",
                 "units": "foo",
                 "time": "foo"
             }
         }

        The switch below would then have cases for "startTime" and "bloodGlucose" and the attributes underneath would
        be handled in that case block.
        */
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

