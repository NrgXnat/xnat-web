package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.nrg.xdat.om.XnatPetsessiondata;

import java.io.IOException;

public class XnatPetsessiondataDeserializer<T extends XnatPetsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 8800846152694684817L;

    @SuppressWarnings("unchecked")
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
            case "id":
                instance.setId(parser.getText());
                break;
            case "stabilization":
                instance.setStabilization(parser.getText());
                break;
            case "studyType":
                instance.setStudytype(parser.getText());
                break;
            case "patientID":
                instance.setPatientid(parser.getText());
                break;
            case "patientName":
                instance.setPatientname(parser.getText());
                break;
            case "tracer":
                instance.setTracer_name(parser.getText());
                break;
            case "start_time":
                instance.setStartTime(parser.getText());
                break;
            case "start_time_scan":
                instance.setStartTimeScan(parser.getText());
                break;
            case "start_time_injection":
                instance.setStartTimeInjection(parser.getText());
                break;
            case "blood_glucose":
                instance.setBloodGlucose(Double.parseDouble(parser.getText()));
                break;
            case "blood_glucose_units":
                instance.setBloodGlucoseUnits(parser.getText());
                break;
            case "blood_glucose_time":
                instance.setBloodGlucoseTime(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
