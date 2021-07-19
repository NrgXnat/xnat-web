package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatPetsessiondata;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

@SuppressWarnings("rawtypes")
public class XnatPetsessiondataDeserializer extends XnatImagesessiondataDeserializer {
	private static final long serialVersionUID = 8800846152694684817L;

	@SuppressWarnings("unchecked")
	public XnatPetsessiondataDeserializer() {
        super(XnatPetsessiondata.class);
    }
   
    @Override
    protected XnatPetsessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatPetsessiondata xnatPetsessiondata = new XnatPetsessiondata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	xnatPetsessiondata.setId(parser.getText());
                    break;
                case "stabilization":
                	xnatPetsessiondata.setStabilization(parser.getText());
                    break;
                case "studyType":
                    xnatPetsessiondata.setStudytype(parser.getText());
                    break;
                case "patientID":
                    xnatPetsessiondata.setPatientid(parser.getText());
                    break;
                case "patientName":
                    xnatPetsessiondata.setPatientname(parser.getText());
                    break;
                case "tracer":
                    xnatPetsessiondata.setTracer_name(parser.getText());
                    break;
                case "start_time":
                    xnatPetsessiondata.setStartTime(parser.getText());
                    break;
                case "start_time_scan":
                    xnatPetsessiondata.setStartTimeScan(parser.getText());
                    break;
                case "start_time_injection":
                    xnatPetsessiondata.setStartTimeInjection(parser.getText());
                    break;
                case "blood_glucose":
                    xnatPetsessiondata.setBloodGlucose(Double.parseDouble(parser.getText()));
                    break;
                case "blood_glucose_units":
                    xnatPetsessiondata.setBloodGlucoseUnits(parser.getText());
                    break;
                case "blood_glucose_time":
                    xnatPetsessiondata.setBloodGlucoseTime(parser.getText());
                    break;
            }
        }
        return (XnatPetsessiondata)super.deserializeImpl(parser, context);
    }

	@Override
	protected XnatPetsessiondata getNewInstance() throws JsonProcessingException {
		return null;
	}

}
