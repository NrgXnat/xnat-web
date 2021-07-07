package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;

import java.io.IOException;

@Slf4j
public class XnatDemographicdataDeserializer extends AbstractBaseElementDeserializer<XnatDemographicdata> {
    public XnatDemographicdataDeserializer() {
        super(XnatDemographicdata.class);
    }

    @Override
    protected XnatDemographicdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatDemographicdata demographics = new XnatDemographicdata();
       
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "dob":
                    demographics.setDob(parseDate(parser.getText()));
                    break;
                case "educationDesc":
                    demographics.setEducationdesc(parser.getText());
                    break;
                case "education":
                    demographics.setEducation(parser.getIntValue());
                    break;
                case "age":
                    demographics.setAge(parser.getIntValue());
                    break;
                case "ses":
                    demographics.setSes(parser.getIntValue());
                    break;
                case "gender":
                    demographics.setGender(parser.getText());
                    break;
                case "handedness":
                    demographics.setHandedness(parser.getText());
                    break;
                case "ethnicity":
                    demographics.setEthnicity(parser.getText());
                    break;
                case "race":
                    demographics.setRace(parser.getText());
                    break;
                case "weight":
                    demographics.setWeight(parser.getDoubleValue());
                    break;
                case "height":
                    demographics.setHeight(parser.getDoubleValue());
                    break;
            }
        }
        return demographics;
    }

	@Override
	protected XnatDemographicdata getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
